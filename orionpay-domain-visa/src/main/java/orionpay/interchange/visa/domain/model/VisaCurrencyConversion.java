package orionpay.interchange.visa.domain.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisaCurrencyConversion {
    private Long id;
    private Long masterId;
    private String originalCurrencyCode;
    private BigDecimal originalAmount;
    private BigDecimal conversionRate;
    private LocalDate conversionDate;
    private BigDecimal settlementAmountBrl;
    private BigDecimal markupFee;
    private String auditUuid;
}
