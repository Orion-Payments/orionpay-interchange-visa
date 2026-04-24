package orionpay.interchange.visa.infrastructure.adapters.output.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import orionpay.interchange.visa.domain.model.VisaAuditBase;
import orionpay.interchange.visa.domain.ports.output.VisaAuditBaseRepositoryPort;
import orionpay.interchange.visa.domain.vo.CorrelationId;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.VisaAuditBaseEntity;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper.VisaAuditBaseMapper;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VisaAuditBaseRepositoryAdapter implements VisaAuditBaseRepositoryPort {

    private final VisaAuditBaseJpaRepository jpaRepository;
    private final VisaAuditBaseMapper mapper;

    @Override
    public VisaAuditBase save(VisaAuditBase auditBase) {
        VisaAuditBaseEntity entity = mapper.toEntity(auditBase);
        VisaAuditBaseEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<VisaAuditBase> findByCorrelationId(CorrelationId correlationId) {
        return jpaRepository.findById(correlationId.value()).map(mapper::toDomain);
    }
}
