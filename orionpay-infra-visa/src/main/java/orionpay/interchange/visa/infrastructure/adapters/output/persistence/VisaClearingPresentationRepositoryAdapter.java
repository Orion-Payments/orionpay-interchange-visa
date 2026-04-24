package orionpay.interchange.visa.infrastructure.adapters.output.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import orionpay.interchange.visa.domain.model.VisaClearingPresentation;
import orionpay.interchange.visa.domain.ports.output.VisaClearingPresentationRepositoryPort;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.VisaClearingPresentationEntity;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper.VisaClearingPresentationMapper;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VisaClearingPresentationRepositoryAdapter implements VisaClearingPresentationRepositoryPort {

    private final VisaClearingPresentationJpaRepository jpaRepository;
    private final VisaClearingPresentationMapper mapper;

    @Override
    public VisaClearingPresentation save(VisaClearingPresentation presentation) {
        VisaClearingPresentationEntity entity = mapper.toEntity(presentation);
        VisaClearingPresentationEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<VisaClearingPresentation> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
