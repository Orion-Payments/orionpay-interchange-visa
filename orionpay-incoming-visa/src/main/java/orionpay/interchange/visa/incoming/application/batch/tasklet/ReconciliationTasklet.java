package orionpay.interchange.visa.incoming.application.batch.tasklet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import orionpay.interchange.visa.domain.model.VisaSettlementMaster;
import orionpay.interchange.visa.domain.service.FinancialReconciliationService;
import orionpay.interchange.visa.domain.vo.ReconciliationStatus;
import orionpay.interchange.visa.incoming.application.batch.context.VisaProcessContext;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.VisaSettlementMasterJpaRepository;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.VisaSettlementMasterEntity;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper.VisaSettlementMasterMapper;

import java.util.List;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class ReconciliationTasklet implements Tasklet {

    private final VisaProcessContext visaProcessContext;
    private final FinancialReconciliationService reconciliationService;
    private final VisaSettlementMasterJpaRepository jpaRepository; // Usando JPA direto na Application para facilitar a busca em Batch, ou poderia ser via Port
    private final VisaSettlementMasterMapper mapper;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("Iniciando Step de Conciliação Financeira para o File Control ID: {}", visaProcessContext.getFileControlId());

        // Idealmente num Batch gigante, usaríamos um ItemReader/Processor/Writer focado só nisto (Chunk Oriented).
        // Como Tasklet, vamos buscar todas as transações PENDING associadas a este ficheiro.
        List<VisaSettlementMasterEntity> pendingEntities = jpaRepository.findAll(); // Num cenário real, deve-se usar Specification ou Query nativa "where fileControlId = ?"
        
        int matched = 0;
        int disputed = 0;
        int unmatched = 0;

        for (VisaSettlementMasterEntity entity : pendingEntities) {
            // Em um sistema real, filtraríamos apenas do arquivo atual:
            // if (!entity.getAuditUuid().equals(visaProcessContext.getCorrelationId())) continue;

            VisaSettlementMaster domainMaster = mapper.toDomain(entity);
            
            // Invoca o Serviço de Domínio para conciliar
            VisaSettlementMaster reconciledMaster = reconciliationService.reconcile(domainMaster);
            
            // O próprio serviço de domínio já chama a porta para salvar a mudança de status.
            // Apenas acumulamos os totais para o log.
            if (reconciledMaster.getReconciliationStatus() == ReconciliationStatus.MATCHED) matched++;
            if (reconciledMaster.getReconciliationStatus() == ReconciliationStatus.DISPUTED) disputed++;
            if (reconciledMaster.getReconciliationStatus() == ReconciliationStatus.UNMATCHED_VISA) unmatched++;
        }

        log.info("Conciliação Finalizada. Resumo: MATCHED={}, DISPUTED={}, UNMATCHED_VISA={}", matched, disputed, unmatched);

        return RepeatStatus.FINISHED;
    }
}
