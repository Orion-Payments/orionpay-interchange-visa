package orionpay.interchange.visa.infrastructure.adapters.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.AuditLogBaseEntity;

@Repository
public interface AuditLogBaseJpaRepository extends JpaRepository<AuditLogBaseEntity, String> {
}
