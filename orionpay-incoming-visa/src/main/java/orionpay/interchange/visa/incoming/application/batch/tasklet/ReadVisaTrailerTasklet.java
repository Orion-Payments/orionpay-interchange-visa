package orionpay.interchange.visa.incoming.application.batch.tasklet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import orionpay.interchange.visa.domain.model.AuditLogBase;
import orionpay.interchange.visa.domain.model.VisaFileControl;
import orionpay.interchange.visa.domain.ports.output.AuditLogBaseRepositoryPort;
import orionpay.interchange.visa.domain.ports.output.VisaFileControlRepositoryPort;
import orionpay.interchange.visa.domain.vo.CorrelationId;
import orionpay.interchange.visa.domain.vo.MoneyAmount;
import orionpay.interchange.visa.incoming.application.batch.context.VisaProcessContext;

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class ReadVisaTrailerTasklet implements Tasklet {

    private final VisaProcessContext visaProcessContext;
    private final VisaFileControlRepositoryPort fileControlRepositoryPort;
    private final AuditLogBaseRepositoryPort auditLogBaseRepositoryPort;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        String fileName = visaProcessContext.getFileName();
        log.info("Iniciando leitura do trailer do arquivo: {}", fileName);
        String lastLine = null;
        String currentLine;

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(fileName))) {
            String line;
            while ((currentLine = reader.readLine()) != null) {
                if (!currentLine.trim().isEmpty()) {
                    lastLine = currentLine;
                }
            }
        }

        // Validação de segurança idêntica à do Header
        if (lastLine == null || lastLine.length() < 27) {
            throw new IllegalStateException("Arquivo sem trailer ou linha 92 muito curta para o padrão Visa.");
        }

        if (!lastLine.startsWith("92")) {
            throw new IllegalStateException("O arquivo não termina com TCR92. Encontrado: " + lastLine.substring(0, 2));
        }

        // Audita a linha do trailer
        CorrelationId correlationId = new CorrelationId(visaProcessContext.getCorrelationId());
        AuditLogBase auditLog = AuditLogBase.builder()
                .correlationId(correlationId)
                .rawLineContent(lastLine)
                .processingStatusId(2) // COMPLETED/SUCCESS
                .statusMessage("Leitura de Trailer (TCR92)")
                .createdAt(LocalDateTime.now())
                .build();
        auditLogBaseRepositoryPort.save(auditLog);

        // Extraindo dados do Trailer (TCR92)
        // Manual VSS genérico:
        // Posições 45-56: Total Destination Amount (com 2 casas decimais implícitas)
        // Posições 57-64: Total Record Count (monetários + não monetários)
        try {

            if (lastLine.length() < 64) {
                throw new IllegalStateException("Linha 92 incompleta para validar totais (mínimo 64 caracteres).");
            }
            String totalAmountStr = lastLine.substring(44, 56).trim();
            BigDecimal expectedAmount = new BigDecimal(totalAmountStr).divide(new BigDecimal(100));

            String totalCountStr = lastLine.substring(56, 64).trim();
            Integer expectedCount = Integer.parseInt(totalCountStr);

            log.info("Trailer lido com sucesso. Valor Esperado: {}, Qtd Esperada: {}", expectedAmount, expectedCount);

            // Recupera o FileControl
            VisaFileControl fileControl = fileControlRepositoryPort.findById(visaProcessContext.getFileControlId())
                    .orElseThrow(() -> new IllegalStateException("FileControl não encontrado para o ID: " + visaProcessContext.getFileControlId()));

            // Atualiza os totais calculados internamente antes de validar
            fileControl.setTotalRecordCount(visaProcessContext.getTotalRecordCount().get());
            fileControl.setTotalAmount(MoneyAmount.of(visaProcessContext.getTotalAmount()));

            // Valida a integridade (Este método muda o status internamente ou lança Exception)
            fileControl.confirmIntegrity(expectedAmount, expectedCount);

            // Salva as mudanças
            fileControlRepositoryPort.save(fileControl);
            log.info("Integridade do arquivo ID {} confirmada com sucesso!", fileControl.getId());

        } catch (Exception e) {
            log.error("Falha ao validar a integridade do trailer do arquivo.", e);
            // Se a validação falhar, atualiza o status para FAILED_INTEGRITY via Exception handling no Listener ou aqui mesmo
            VisaFileControl fileControl = fileControlRepositoryPort.findById(visaProcessContext.getFileControlId()).orElse(null);
            if (fileControl != null) {
                fileControl.updateStatus("FAILED_INTEGRITY");
                fileControlRepositoryPort.save(fileControl);
            }
            throw e; // Aborta o Job
        }

        return RepeatStatus.FINISHED;
    }
}
