package orionpay.interchange.visa.infrastructure.adapters.output.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import orionpay.interchange.visa.domain.model.VisaFileTrailer;
import orionpay.interchange.visa.domain.ports.output.VisaFileTrailerRepositoryPort;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.VisaFileTrailerEntity;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper.VisaFileTrailerMapper;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VisaFileTrailerRepositoryAdapter implements VisaFileTrailerRepositoryPort {

    private final VisaFileTrailerJpaRepository jpaRepository;
    private final VisaFileTrailerMapper mapper;

    @Override
    public VisaFileTrailer save(VisaFileTrailer trailer) {
        VisaFileTrailerEntity entity = mapper.toEntity(trailer);
        VisaFileTrailerEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<VisaFileTrailer> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
