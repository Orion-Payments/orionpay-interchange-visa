package orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "visa_settlement_details", schema = "visa_interchange")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VisaSettlementDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "master_settlement_id")
    private Long masterSettlementId;

    @Column(name = "merchant_dba_name", length = 75)
    private String merchantDbaName;

    @Column(name = "merchant_legal_name", length = 75)
    private String merchantLegalName;

    @Column(name = "merchant_address_line1", length = 60)
    private String merchantAddressLine1;

    @Column(name = "merchant_address_line2", length = 60)
    private String merchantAddressLine2;

    @Column(name = "merchant_city", length = 29)
    private String merchantCity;

    @Column(name = "mcc_primary", length = 4)
    private String mccPrimary;

    @Column(name = "mcc_secondary", length = 4)
    private String mccSecondary;

    @Column(name = "acquiring_bin_1", length = 6)
    private String acquiringBin1;

    @Column(name = "card_acceptor_id_1", length = 15)
    private String cardAcceptorId1;

    @Column(name = "acquiring_bin_2", length = 6)
    private String acquiringBin2;

    @Column(name = "card_acceptor_id_2", length = 15)
    private String cardAcceptorId2;
}
