package orionpay.interchange.visa.domain.ports.output;

import orionpay.interchange.visa.domain.model.AuditLogBase;
import orionpay.interchange.visa.domain.vo.CorrelationId;
import java.util.Optional;

public interface AuditLogBaseRepositoryPort {
    AuditLogBase save(AuditLogBase log);
    Optional<AuditLogBase> findByCorrelationId(CorrelationId correlationId);
}
