package orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "visa_audit_base", schema = "visa_interchange")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VisaAuditBaseEntity {

    @Id
    @Column(name = "uuid", length = 36)
    private String uuid;

    @Column(name = "raw_text_row", length = 170)
    private String rawTextRow;

    @Column(name = "processing_status_code")
    private Integer processingStatusCode;

    @Column(name = "processing_description", columnDefinition = "text")
    private String processingDescription;

    @Column(name = "date_created", nullable = false)
    private LocalDateTime dateCreated;

    @Column(name = "date_updated")
    private LocalDateTime dateUpdated;

    @PrePersist
    protected void onPersist() {
        if (this.dateCreated == null) this.dateCreated = LocalDateTime.now();
    }
}
