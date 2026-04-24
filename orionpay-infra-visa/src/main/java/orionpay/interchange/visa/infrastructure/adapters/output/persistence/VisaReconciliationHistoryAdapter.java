package orionpay.interchange.visa.infrastructure.adapters.output.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import orionpay.interchange.visa.domain.model.VisaReconciliationHistory;
import orionpay.interchange.visa.domain.ports.output.VisaReconciliationHistoryPort;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.VisaReconciliationHistoryEntity;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper.VisaReconciliationHistoryMapper;

@Component
@RequiredArgsConstructor
public class VisaReconciliationHistoryAdapter implements VisaReconciliationHistoryPort {

    private final VisaReconciliationHistoryJpaRepository jpaRepository;
    private final VisaReconciliationHistoryMapper mapper;

    @Override
    public VisaReconciliationHistory save(VisaReconciliationHistory history) {
        VisaReconciliationHistoryEntity entity = mapper.toEntity(history);
        VisaReconciliationHistoryEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
}
