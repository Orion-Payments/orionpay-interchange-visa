package orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "visa_dispute_management", schema = "visa_interchange")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VisaDisputeManagementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "return_reason_code_primary", length = 3)
    private String returnReasonCodePrimary;

    @Column(name = "return_reason_code_secondary", length = 3)
    private String returnReasonCodeSecondary;

    @Column(name = "original_source_amount", precision = 19, scale = 2)
    private BigDecimal originalSourceAmount;

    @Column(name = "original_source_currency", length = 3)
    private String originalSourceCurrency;

    @Column(name = "fee_program_indicator_submitted", length = 3)
    private String feeProgramIndicatorSubmitted;

    @Column(name = "fee_program_indicator_assessed", length = 3)
    private String feeProgramIndicatorAssessed;

    @Column(name = "interchange_fee_amount", precision = 19, scale = 2)
    private BigDecimal interchangeFeeAmount;

    @Column(name = "interchange_fee_sign", length = 1)
    private String interchangeFeeSign;
}
