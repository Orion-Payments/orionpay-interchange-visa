package orionpay.interchange.visa.domain.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisaDisputeDetailsBrazil {
    private Long id;
    private Long masterId;
    private BigDecimal nationalReimbursementFee;
    private String settlementType;
    private String countryCode;
    private OffsetDateTime createdAt;
}
