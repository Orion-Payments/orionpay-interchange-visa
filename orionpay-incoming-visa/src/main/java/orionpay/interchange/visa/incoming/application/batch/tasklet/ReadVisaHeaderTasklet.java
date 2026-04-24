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
import orionpay.interchange.visa.domain.ports.output.AuditLogBaseRepositoryPort;
import orionpay.interchange.visa.domain.ports.output.VisaFileControlRepositoryPort;
import orionpay.interchange.visa.domain.vo.CorrelationId;
import orionpay.interchange.visa.incoming.application.batch.context.VisaProcessContext;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class ReadVisaHeaderTasklet implements Tasklet {

    private final VisaProcessContext visaProcessContext;
    private final VisaFileControlRepositoryPort fileControlRepositoryPort;
    private final AuditLogBaseRepositoryPort auditLogBaseRepositoryPort;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        String filePath = visaProcessContext.getFileName();
        log.info("Lendo Header oficial Visa: {}", filePath);

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String headerLine = reader.readLine();

            // Validação de integridade física da linha
            if (headerLine == null || headerLine.length() < 27) {
                throw new IllegalStateException("Header inválido ou muito curto para o padrão Visa.");
            }

            // Padrão TCR90
            String tcrCode = headerLine.substring(0, 2);
            if (!"90".equals(tcrCode)) {
                throw new IllegalStateException("O arquivo não inicia com TCR90 (Header).");
            }

            // Extração baseada no layout fixo
            String sourceBin = headerLine.substring(10, 16).trim();
            String transmissionDateStr = headerLine.substring(19, 27).trim();

            // Auditoria do recebimento
            saveAudit(headerLine);

            // Conversão da Data (Formato YYYYMMDD)
            LocalDate transmissionDate = LocalDate.parse(transmissionDateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));

            log.info("Header Processado -> BIN: {}, Data: {}", sourceBin, transmissionDate);

            // Atualiza o domínio com os dados do arquivo real
            updateFileControl(sourceBin, transmissionDate);

        } catch (Exception e) {
            log.error("Erro fatal no processamento do Header Visa: {}", e.getMessage());
            throw e;
        }

        return RepeatStatus.FINISHED;
    }

    private void saveAudit(String line) {
        CorrelationId correlationId = new CorrelationId(visaProcessContext.getCorrelationId());
        AuditLogBase audit = AuditLogBase.builder()
                .correlationId(correlationId)
                .rawLineContent(line)
                .processingStatusId(2)
                .statusMessage("Header TCR90 Processado")
                .createdAt(LocalDateTime.now())
                .build();
        auditLogBaseRepositoryPort.save(audit);
    }

    private void updateFileControl(String bin, LocalDate date) {
        fileControlRepositoryPort.findById(visaProcessContext.getFileControlId()).ifPresent(control -> {
            control.setSourceBin(bin);
            control.setTransmissionDate(date);
            fileControlRepositoryPort.save(control);
        });
    }
}