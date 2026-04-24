package orionpay.interchange.visa.application.dto;

import lombok.*;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisaAuditBaseDTO {
    private String correlationId;
    private String rawTextRow;
    private Integer processingStatusCode;
    private String processingDescription;
    private OffsetDateTime dateCreated;
    private OffsetDateTime dateUpdated;
}
