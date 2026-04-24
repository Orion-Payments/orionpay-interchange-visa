package orionpay.interchange.visa.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import orionpay.interchange.visa.domain.vo.ReconciliationStatus;

import java.time.LocalDateTime;

/**
 * Entidade de auditoria para as mudanças de status da conciliação (para visibilidade de caixa).
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisaReconciliationHistory {
    private Long id;
    private Long settlementMasterId;
    private ReconciliationStatus oldStatus;
    private ReconciliationStatus newStatus;
    private String reason;
    private LocalDateTime changedAt;
}
