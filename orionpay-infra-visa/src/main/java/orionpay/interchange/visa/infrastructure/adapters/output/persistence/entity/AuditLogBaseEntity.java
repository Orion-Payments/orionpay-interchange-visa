package orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log_base", schema = "visa_interchange")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogBaseEntity {

    @Id
    @Column(name = "correlation_uuid", length = 36)
    private String correlationUuid;

    @Column(name = "raw_line_content", length = 170)
    private String rawLineContent;

    @Column(name = "processing_status_id")
    private Integer processingStatusId;

    @Column(name = "status_message", columnDefinition = "text")
    private String statusMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onPersist() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
    }
}
