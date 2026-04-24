package orionpay.interchange.visa.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import orionpay.interchange.visa.domain.vo.CorrelationId;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisaAuditBase {

    private CorrelationId correlationId;
    private String rawTextRow;
    private Integer processingStatusCode;
    private String processingDescription;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;

    public void markAsProcessed(String description) {
        this.processingStatusCode = 2; // Assuming 2 = Success
        this.processingDescription = description;
        this.dateUpdated = LocalDateTime.now();
    }

    public void markAsFailed(String errorDescription) {
        this.processingStatusCode = 3; // Assuming 3 = Error
        this.processingDescription = errorDescription;
        this.dateUpdated = LocalDateTime.now();
    }
}
