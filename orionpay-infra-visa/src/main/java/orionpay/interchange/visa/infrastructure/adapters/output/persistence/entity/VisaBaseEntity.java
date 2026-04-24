package orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class VisaBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "uuid", nullable = false, length = 36, unique = true)
    protected String uuid;

    @Column(name = "created_at", nullable = false, updatable = false)
    protected OffsetDateTime createdAt;

    @Column(name = "updated_at")
    protected OffsetDateTime updatedAt;

    @PrePersist
    protected void onPersist() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID().toString();
        }
        if (this.createdAt == null) {
            this.createdAt = OffsetDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
