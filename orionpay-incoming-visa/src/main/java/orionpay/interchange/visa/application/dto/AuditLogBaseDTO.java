package orionpay.interchange.visa.application.dto;

import lombok.*;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogBaseDTO {
    private String correlationId;
    private String rawLineContent;
    private Integer processingStatusId;
    private String statusMessage;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
