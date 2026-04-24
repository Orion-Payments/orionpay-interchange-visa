package orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "visa_national_reversal_details", schema = "visa_interchange")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VisaNationalReversalDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "clearing_record_id")
    private Long clearingRecordId;

    @Column(name = "national_reimbursement_fee", precision = 19, scale = 2)
    private BigDecimal nationalReimbursementFee;

    @Column(name = "settlement_type", length = 3)
    private String settlementType;

    @Column(name = "reason_code", length = 4)
    private String reasonCode;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
