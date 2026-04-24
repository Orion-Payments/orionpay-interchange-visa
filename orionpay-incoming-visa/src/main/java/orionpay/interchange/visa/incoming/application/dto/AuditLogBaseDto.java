package orionpay.interchange.visa.incoming.application.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuditLogBaseDto {
    private String correlationId;
    private String rawLineContent;
    private Integer processingStatusId;
    private String statusMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
