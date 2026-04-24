package orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "visa_clearing_presentation", schema = "visa_interchange")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VisaClearingPresentationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "country_code", length = 3)
    private String countryCode;

    @Column(name = "settlement_type", length = 3)
    private String settlementType;

    @Column(name = "national_reimbursement_fee", precision = 19, scale = 2)
    private BigDecimal nationalReimbursementFee;

    @Column(name = "installment_count")
    private Integer installmentCount;

    @Column(name = "transaction_type", length = 2)
    private String transactionType;

    @Column(name = "card_sequence_number", length = 3)
    private String cardSequenceNumber;

    @Column(name = "terminal_verification_results", length = 10)
    private String terminalVerificationResults;

    @Column(name = "cryptogram_amount", precision = 19, scale = 2)
    private BigDecimal cryptogramAmount;

    @Column(name = "issuer_application_data", columnDefinition = "TEXT")
    private String issuerApplicationData;
}
