package orionpay.interchange.visa.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import orionpay.interchange.visa.domain.vo.MoneyAmount;

import java.time.LocalDateTime;

/**
 * Modelo de domínio para detalhes específicos do intercâmbio Nacional (Brasil).
 * Pode conter as taxas nacionais de reembolso e outras exigências regulatórias.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisaNationalReversalDetails {
    private Long id;
    private Long clearingRecordId; // ID referenciando transações ou apresentações do arquivo
    private MoneyAmount nationalReimbursementFee; // Taxa de intercâmbio nacional
    private String settlementType; // Tipo (D=Débito, C=Crédito)
    private String reasonCode; // Código indicando o porquê do estorno (Reversal)
    private LocalDateTime createdAt;
}
