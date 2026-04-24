package orionpay.interchange.visa.incoming.application.batch.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;
import orionpay.interchange.visa.domain.model.VisaFileControl;
import orionpay.interchange.visa.domain.ports.output.VisaFileControlRepositoryPort;
import orionpay.interchange.visa.domain.vo.CorrelationId;
import orionpay.interchange.visa.domain.vo.MoneyAmount;
import orionpay.interchange.visa.incoming.application.batch.context.VisaProcessContext;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class VisaJobListener implements JobExecutionListener {

    private final VisaProcessContext visaProcessContext;
    private final VisaFileControlRepositoryPort fileControlRepositoryPort;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        String fileName = jobExecution.getJobParameters().getString("fileName");
        log.info("--- [VisaJobListener] Inicializando Job para o arquivo: {} ---", fileName);
        
        visaProcessContext.setFileName(fileName);
        
        String generatedCorrelationId = java.util.UUID.randomUUID().toString();
        visaProcessContext.setCorrelationId(generatedCorrelationId);
        
        // 1. Criar Registo de Ficheiro com Status PROCESSING
        VisaFileControl fileControl = VisaFileControl.builder()
                .auditUuid(new CorrelationId(generatedCorrelationId))
                .fileName(fileName)
                .fileTypeIndicator("TC50") // Fixo para liquidação (ou dinâmico se ler de cabeçalho)
                .sourceBin("000000") // Se não lermos do FileHeader, mockamos o sourceBin temporariamente
                .transmissionDate(LocalDate.now())
                .totalRecordCount(0)
                .totalAmount(MoneyAmount.of(BigDecimal.ZERO))
                .statusName("PROCESSING")
                .controlMovementDate(LocalDate.now())
                .build();

        // 2. Salvar Ficheiro na BD e guardar o ID no contexto para uso posterior
        VisaFileControl savedControl = fileControlRepositoryPort.save(fileControl);
        visaProcessContext.setFileControlId(savedControl.getId());

        log.info("--- [VisaJobListener] Arquivo registado com File Control ID: {} e UUID: {} ---", 
                savedControl.getId(), generatedCorrelationId);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("--- [VisaJobListener] Job finalizado com status: {} ---", jobExecution.getStatus());

        Long controlId = visaProcessContext.getFileControlId();
        if (controlId == null) return;

        fileControlRepositoryPort.findById(controlId).ifPresent(control -> {
            // Se o Batch falhou em qualquer Step (inclusive no Chunk), marca como FAILED
            if (jobExecution.getStatus().isUnsuccessful()) {
                control.updateStatus("FAILED");
            } else {
                // Sincroniza os totais finais do contexto para a entidade de banco
                int totalRecords = visaProcessContext.getTotalRecordCount().get();
                BigDecimal totalAmount = visaProcessContext.getTotalAmount();

                // Log de depuração crítico para você ver no console se o contador saiu do zero
                log.info(">>> Totais calculados no Contexto: Qtd={}, Valor={}", totalRecords, totalAmount);

                control.setTotalRecordCount(totalRecords);
                control.setTotalAmount(MoneyAmount.of(totalAmount));

                // Só marca como COMPLETED se a integridade já foi validada na Tasklet do Trailer
                if ("PROCESSING".equals(control.getStatusName())) {
                    control.updateStatus("COMPLETED");
                }
            }

            fileControlRepositoryPort.save(control);
            log.info("--- [VisaJobListener] Persistência final concluída para o Arquivo ID {} ---", control.getId());
        });
    }
}
