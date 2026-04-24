package orionpay.interchange.visa.incoming.application.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VisaAuditBaseDto {
    private String correlationId;
    private String rawTextRow;
    private Integer processingStatusCode;
    private String processingDescription;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
}
