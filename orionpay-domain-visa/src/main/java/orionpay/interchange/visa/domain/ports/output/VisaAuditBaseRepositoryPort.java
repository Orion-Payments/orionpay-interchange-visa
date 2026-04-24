package orionpay.interchange.visa.domain.ports.output;

import orionpay.interchange.visa.domain.model.VisaAuditBase;
import orionpay.interchange.visa.domain.vo.CorrelationId;
import java.util.Optional;

public interface VisaAuditBaseRepositoryPort {
    VisaAuditBase save(VisaAuditBase auditBase);
    Optional<VisaAuditBase> findByCorrelationId(CorrelationId correlationId);
}
