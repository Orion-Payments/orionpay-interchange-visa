package orionpay.interchange.visa.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import orionpay.interchange.visa.domain.vo.MoneyAmount;

/**
 * Modelo de domínio para a gestão de disputas (Chargebacks, Reversals) da Visa.
 * Normalmente associado a transações TC50 com Usage Code específico ou a TC10/20.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisaDisputeManagement {
    private Long id;
    private String returnReasonCodePrimary;
    private String returnReasonCodeSecondary;
    private MoneyAmount originalSourceAmount;
    private String feeProgramIndicatorSubmitted;
    private String feeProgramIndicatorAssessed;
    private MoneyAmount interchangeFeeAmount;
    private String interchangeFeeSign; // Pode ser 'D' (Debito) ou 'C' (Credito)

    /**
     * Devolve o valor do interchange fee formatado de acordo com o sinal (positivo para Crédito, negativo para Débito).
     */
    public MoneyAmount getSignedInterchangeFee() {
        if (interchangeFeeAmount == null || interchangeFeeAmount.value() == null) {
            return null;
        }
        if ("D".equalsIgnoreCase(interchangeFeeSign)) {
            return new MoneyAmount(interchangeFeeAmount.value().negate(), interchangeFeeAmount.currencyCode());
        }
        return interchangeFeeAmount; // Crédito ou sem sinal definido mantém positivo
    }
}
