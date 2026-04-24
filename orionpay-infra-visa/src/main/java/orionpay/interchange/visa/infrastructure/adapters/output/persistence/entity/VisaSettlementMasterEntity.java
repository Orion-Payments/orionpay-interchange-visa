package orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "visa_settlement_master", schema = "visa_interchange",
       indexes = {
           @Index(name = "idx_visa_master_bin", columnList = "card_bin"),
           @Index(name = "idx_visa_master_date", columnList = "settlement_date"),
           @Index(name = "idx_visa_settlement_rrn", columnList = "rrn_number")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VisaSettlementMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_control_id")
    private Long fileControlId;

    @Column(name = "transaction_id_orionpay", nullable = false, length = 50)
    private String transactionIdOrionpay;

    @Column(name = "card_bin", nullable = false, length = 8)
    private String cardBin;

    @Column(name = "merchant_id_visa", length = 15)
    private String merchantIdVisa;

    @Column(name = "amount_gross", nullable = false, precision = 19, scale = 2)
    private BigDecimal amountGross;

    @Column(name = "amount_interchange", precision = 19, scale = 2)
    private BigDecimal amountInterchange;

    @Column(name = "currency_code", length = 3)
    private String currencyCode;

    @Column(name = "date_transaction")
    private LocalDate transactionDate;

    @Column(name = "settlement_date")
    private LocalDate settlementDate;

    @Column(name = "auth_code", length = 6)
    private String authCode;

    @Column(name = "rrn_number", length = 12)
    private String rrnNumber;

    @Column(name = "audit_uuid", nullable = false, length = 36)
    private String auditUuid;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "usage_code", length = 1)
    private String usageCode;

    @Column(name = "reason_code", length = 4)
    private String reasonCode;

    @Column(name = "settlement_flag", length = 1)
    private String settlementFlag;

    @PrePersist
    protected void onPersist() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.currencyCode == null) this.currencyCode = "986";
    }
}
