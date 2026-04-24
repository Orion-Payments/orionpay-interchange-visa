package orionpay.interchange.visa.incoming.application.batch.tasklet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import orionpay.interchange.visa.domain.model.AuditLogBase;
import orionpay.interchange.visa.domain.model.VisaSettlementMaster;
import orionpay.interchange.visa.domain.ports.output.AuditLogBaseRepositoryPort;
import orionpay.interchange.visa.domain.usecase.ProcessVisaSettlementUseCase;
import orionpay.interchange.visa.domain.vo.CorrelationId;
import orionpay.interchange.visa.domain.vo.MoneyAmount;
import orionpay.interchange.visa.incoming.application.batch.context.VisaProcessContext;
import orionpay.interchange.visa.incoming.application.dto.TC50TCR0Dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class VisaSettlementTasklet implements Tasklet {

    private final ProcessVisaSettlementUseCase processVisaSettlementUseCase;
    private final VisaProcessContext visaProcessContext;
    private final FlatFileItemReader<TC50TCR0Dto> tc50ItemReader; // Injetando o leitor fixo
    private final AuditLogBaseRepositoryPort auditLogBaseRepositoryPort;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        
        String fileName = chunkContext.getStepContext().getStepExecution()
                .getJobExecution().getJobParameters().getString("fileName");
                
        // Certifica-se de que o contexto tem os dados (poderia já ter vindo da Tasklet anterior)
        if (visaProcessContext.getFileName() == null) {
            visaProcessContext.setFileName(fileName);
        }
        
        log.info("Iniciando processamento real do arquivo no Tasklet: {}", visaProcessContext.getFileName());
        
        String correlationIdStr = visaProcessContext.getCorrelationId();
        if (correlationIdStr == null) {
            correlationIdStr = UUID.randomUUID().toString();
            visaProcessContext.setCorrelationId(correlationIdStr);
        }

        CorrelationId correlationId = new CorrelationId(correlationIdStr);

        // Configurando o recurso do leitor dinamicamente com o arquivo
        tc50ItemReader.setResource(new FileSystemResource(visaProcessContext.getFileName()));
        tc50ItemReader.open(new ExecutionContext()); // Abrindo o arquivo para leitura

        int localRecordCount = 0;
        BigDecimal localTotalAmount = BigDecimal.ZERO;
        int errorCount = 0;

        try {
            TC50TCR0Dto lineDto = null;
            while (true) {
                try {
                    lineDto = tc50ItemReader.read();
                    if (lineDto == null) {
                        break; // Fim do arquivo
                    }

                    // Apenas processa liquidações TC50
                    if (!"50".equals(lineDto.getTransactionCode())) {
                        continue; // Ignora cabeçalhos, trailers ou outras transações (ex: 05, 10, 20)
                    }

                    // 1. Parsing do Valor (Assume-se que os últimos 2 dígitos são decimais - formato comum de clearing)
                    BigDecimal rawAmountValue = new BigDecimal(lineDto.getAmount().trim()).divide(new BigDecimal(100));
                    MoneyAmount grossAmount = MoneyAmount.of(rawAmountValue);

                    // No TC50 (TCR0), a taxa de intercâmbio (interchangeFee) pode não vir na mesma linha
                    // ou vir num range específico dependendo do Usage Code. Aqui, definiremos zero.
                    MoneyAmount interchangeFee = MoneyAmount.of(BigDecimal.ZERO);

                    // 2. Parsing da Data (Formato MMDD em clearing)
                    LocalDate settlementDate = parseVisaDate(lineDto.getSettlementDate());

                    // 3. Construção do Domínio Rico (Sem conhecer Spring Batch)
                    VisaSettlementMaster settlementMaster = new VisaSettlementMaster(
                            correlationId,
                            lineDto.getDestinationBin().trim(), // cardBin
                            grossAmount,
                            interchangeFee,
                            settlementDate,
                            lineDto.getUsageCode() != null ? lineDto.getUsageCode().trim() : "",
                            lineDto.getReasonCode() != null ? lineDto.getReasonCode().trim() : ""
                    );

                    settlementMaster.setTransactionIdOrionpay(UUID.randomUUID().toString());
                    settlementMaster.setAuthCode(UUID.randomUUID().toString().substring(0, 6));

                    // 4. Executa a orquestração do Caso de Uso, passando a linha bruta (rawLine)
                    log.info("Processando BIN: {} - Valor: {}", settlementMaster.getCardBin(), settlementMaster.getGrossAmount().value());
                    processVisaSettlementUseCase.execute(settlementMaster, lineDto.getRawLine());

                    // 5. Atualiza os totais apenas se sucesso
                    localRecordCount++;
                    localTotalAmount = localTotalAmount.add(rawAmountValue);

                } catch (Exception e) {
                    errorCount++;
                    log.error("Erro ao processar linha do arquivo. Linha ignorada. Detalhes: {}", e.getMessage());
                    
                    AuditLogBase errorLog = AuditLogBase.builder()
                            .correlationId(correlationId)
                            .rawLineContent(lineDto != null ? lineDto.getRawLine() : "Desconhecido")
                            .processingStatusId(3) // 3 = FAILED
                            .statusMessage("Erro de parsing/validação (Skip): " + e.getMessage())
                            .createdAt(LocalDateTime.now())
                            .build();
                            
                    auditLogBaseRepositoryPort.save(errorLog);
                }
            }
            
            // Grava os totais consolidados no Contexto do Job
            visaProcessContext.getTotalRecordCount().addAndGet(localRecordCount);
            visaProcessContext.addTotalAmount(localTotalAmount);
            
            log.info("Fim da leitura. Processados: {} registros. Valor total: {}. Erros ignorados (Skips): {}", 
                    localRecordCount, localTotalAmount, errorCount);

        } finally {
            tc50ItemReader.close(); // Garante o fecho do recurso
        }

        return RepeatStatus.FINISHED;
    }

    /**
     * Auxiliar para tratar datas no formato MMDD, comum nos TCs da Visa.
     * Como não há ano, normalmente assume-se o ano corrente ou lógica de virada de ano.
     */
    private LocalDate parseVisaDate(String mmdd) {
        if (mmdd == null || mmdd.trim().length() < 4) {
            return LocalDate.now(); // Fallback
        }
        int currentYear = LocalDate.now().getYear();
        String dateString = currentYear + "-" + mmdd.substring(0, 2) + "-" + mmdd.substring(2, 4);
        try {
            return LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            log.warn("Falha ao analisar data Visa: {}. Usando data atual.", mmdd);
            return LocalDate.now();
        }
    }
}
