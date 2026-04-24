package orionpay.interchange.visa.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import orionpay.interchange.visa.domain.vo.MoneyAmount;

/**
 * Modelo de domínio para dados adicionais da transação (Clearing Presentation).
 * Normalmente enviado via TCR1, TCR3 ou registros específicos do TC50/TC10 para detalhar o EMV, Parcelamento (Installments) etc.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisaClearingPresentation {
    private Long id;
    private String countryCode;
    private String settlementType;
    private MoneyAmount nationalReimbursementFee;
    private Integer installmentCount;
    private String transactionType;
    private String cardSequenceNumber;
    private String terminalVerificationResults;
    private MoneyAmount cryptogramAmount;
    private String issuerApplicationData;

    /**
     * Valida se a transação possui parcelas ativas no Brasil (Ex: Installment count > 0)
     */
    public boolean hasInstallments() {
        return installmentCount != null && installmentCount > 0;
    }
}
