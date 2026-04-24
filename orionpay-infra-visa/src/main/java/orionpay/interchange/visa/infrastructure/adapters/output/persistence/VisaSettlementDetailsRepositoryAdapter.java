package orionpay.interchange.visa.infrastructure.adapters.output.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import orionpay.interchange.visa.domain.model.VisaSettlementDetails;
import orionpay.interchange.visa.domain.ports.output.VisaSettlementDetailsRepositoryPort;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.VisaSettlementDetailsEntity;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper.VisaSettlementDetailsMapper;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VisaSettlementDetailsRepositoryAdapter implements VisaSettlementDetailsRepositoryPort {

    private final VisaSettlementDetailsJpaRepository jpaRepository;
    private final VisaSettlementDetailsMapper mapper;

    @Override
    public VisaSettlementDetails save(VisaSettlementDetails settlementDetails) {
        VisaSettlementDetailsEntity entity = mapper.toEntity(settlementDetails);
        VisaSettlementDetailsEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<VisaSettlementDetails> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
