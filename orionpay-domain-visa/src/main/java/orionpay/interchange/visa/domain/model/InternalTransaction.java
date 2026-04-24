package orionpay.interchange.visa.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import orionpay.interchange.visa.domain.vo.MoneyAmount;

/**
 * Representa a transação interna da OrionPay (autorizada pelo Merchant Service).
 * Pode vir de outra tabela ou de outro microserviço via API.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalTransaction {
    private String rrn;
    private String authCode;
    private MoneyAmount authorizedAmount;
    private String status; // Ex: AUTHORIZED, CAPTURED
}
