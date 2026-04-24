package orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import orionpay.interchange.visa.domain.vo.ReconciliationStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "visa_reconciliation_history", schema = "visa_interchange")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VisaReconciliationHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "settlement_master_id", nullable = false)
    private Long settlementMasterId;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", length = 20)
    private ReconciliationStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", length = 20, nullable = false)
    private ReconciliationStatus newStatus;

    @Column(name = "reason", length = 255)
    private String reason;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @PrePersist
    protected void onPersist() {
        if (this.changedAt == null) this.changedAt = LocalDateTime.now();
    }
}
