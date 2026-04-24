package orionpay.interchange.visa.infrastructure.adapters.output.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import orionpay.interchange.visa.domain.model.VisaNationalReversalDetails;
import orionpay.interchange.visa.domain.ports.output.VisaNationalReversalDetailsRepositoryPort;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.VisaNationalReversalDetailsEntity;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper.VisaNationalReversalDetailsMapper;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VisaNationalReversalDetailsRepositoryAdapter implements VisaNationalReversalDetailsRepositoryPort {

    private final VisaNationalReversalDetailsJpaRepository jpaRepository;
    private final VisaNationalReversalDetailsMapper mapper;

    @Override
    public VisaNationalReversalDetails save(VisaNationalReversalDetails reversalDetails) {
        VisaNationalReversalDetailsEntity entity = mapper.toEntity(reversalDetails);
        VisaNationalReversalDetailsEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<VisaNationalReversalDetails> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
