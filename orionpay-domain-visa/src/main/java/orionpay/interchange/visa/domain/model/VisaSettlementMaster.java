package orionpay.interchange.visa.domain.model;

import lombok.Getter;
import lombok.Setter;
import orionpay.interchange.visa.domain.vo.CorrelationId;
import orionpay.interchange.visa.domain.vo.MoneyAmount;
import orionpay.interchange.visa.domain.vo.ReconciliationStatus;

import java.time.LocalDate;

@Getter
public class VisaSettlementMaster {

    @Setter
    private Long id;

    // File Control relation
    @Setter
    private Long fileControlId;

    @Setter
    private String transactionIdOrionpay;

    @Setter
    private final CorrelationId auditUuid;
    private final String cardBin;
    private final MoneyAmount grossAmount;
    private final MoneyAmount interchangeFee;
    private final LocalDate settlementDate;
    private ReconciliationStatus reconciliationStatus;

    // Novos campos de enriquecimento TC50
    private final String usageCode;
    private final String reasonCode;

    // Campos para conciliação
    @Setter
    private String authCode;
    @Setter
    private String rrnNumber;

    public VisaSettlementMaster(
            CorrelationId auditUuid,
            String cardBin,
            MoneyAmount grossAmount,
            MoneyAmount interchangeFee,
            LocalDate settlementDate,
            String usageCode,
            String reasonCode
    ) {
        this.auditUuid = auditUuid;
        this.cardBin = cardBin;
        this.grossAmount = grossAmount;
        this.interchangeFee = interchangeFee;
        this.settlementDate = settlementDate;
        this.usageCode = usageCode;
        this.reasonCode = reasonCode;
        this.reconciliationStatus = ReconciliationStatus.PENDING; // Regra: Iniciar sempre com PENDING
    }

    /**
     * Devolve o valor líquido (Gross - Interchange)
     */
    public MoneyAmount calculateNetAmount() {
        if (grossAmount == null) {
            throw new IllegalStateException("Gross amount is missing");
        }
        if (interchangeFee == null) {
            return grossAmount;
        }
        return new MoneyAmount(
                grossAmount.value().subtract(interchangeFee.value()),
                grossAmount.currencyCode()
        );
    }

    /**
     * Atualiza o status de reconciliação
     */
    public void changeReconciliationStatus(ReconciliationStatus newStatus) {
        this.reconciliationStatus = newStatus;
    }
}
