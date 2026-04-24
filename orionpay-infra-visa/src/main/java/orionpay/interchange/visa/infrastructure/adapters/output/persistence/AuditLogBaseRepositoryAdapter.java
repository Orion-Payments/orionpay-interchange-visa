package orionpay.interchange.visa.infrastructure.adapters.output.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import orionpay.interchange.visa.domain.model.AuditLogBase;
import orionpay.interchange.visa.domain.ports.output.AuditLogBaseRepositoryPort;
import orionpay.interchange.visa.domain.vo.CorrelationId;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.AuditLogBaseEntity;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper.AuditLogBaseMapper;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuditLogBaseRepositoryAdapter implements AuditLogBaseRepositoryPort {

    private final AuditLogBaseJpaRepository jpaRepository;
    private final AuditLogBaseMapper mapper;

    @Override
    public AuditLogBase save(AuditLogBase log) {
        AuditLogBaseEntity entity = mapper.toEntity(log);
        AuditLogBaseEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<AuditLogBase> findByCorrelationId(CorrelationId correlationId) {
        return jpaRepository.findById(correlationId.value())
                .map(mapper::toDomain);
    }
}
