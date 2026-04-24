package orionpay.interchange.visa.domain.usecase;

import lombok.RequiredArgsConstructor;
import orionpay.interchange.visa.domain.model.AuditLogBase;
import orionpay.interchange.visa.domain.model.VisaSettlementMaster;
import orionpay.interchange.visa.domain.ports.output.AuditLogBaseRepositoryPort;
import orionpay.interchange.visa.domain.ports.output.VisaSettlementRepositoryPort;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class ProcessVisaSettlementUseCase {

    private final VisaSettlementRepositoryPort settlementRepositoryPort;
    private final AuditLogBaseRepositoryPort auditLogBaseRepositoryPort;

    public VisaSettlementMaster execute(VisaSettlementMaster settlement, String rawLine) {
        // 1. Validação
        if (settlement.getGrossAmount().value().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Gross amount must be positive for settlement processing.");
        }

        // 2. Registo de Auditoria
        AuditLogBase auditLog = AuditLogBase.builder()
                .correlationId(settlement.getAuditUuid())
                .rawLineContent(rawLine)
                .processingStatusId(1) // 1 = PROCESSING
                .statusMessage("Iniciando processamento da linha TC50")
                .createdAt(LocalDateTime.now())
                .build();
        
        auditLogBaseRepositoryPort.save(auditLog);

        try {
            // 3. Persistência do Domínio
            VisaSettlementMaster savedSettlement = settlementRepositoryPort.save(settlement);
            
            // 4. Atualização da Auditoria (Sucesso)
            auditLog.markAsProcessed("Processamento TC50 concluído com sucesso");
            auditLogBaseRepositoryPort.save(auditLog);
            
            return savedSettlement;
        } catch (Exception e) {
            // Em caso de falha, atualiza a auditoria e propaga o erro
            auditLog.markAsFailed("Falha no processamento: " + e.getMessage());
            auditLogBaseRepositoryPort.save(auditLog);
            throw e;
        }
    }
}
