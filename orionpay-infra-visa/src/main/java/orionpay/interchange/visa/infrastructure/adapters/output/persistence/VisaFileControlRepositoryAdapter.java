package orionpay.interchange.visa.infrastructure.adapters.output.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import orionpay.interchange.visa.domain.model.VisaFileControl;
import orionpay.interchange.visa.domain.ports.output.VisaFileControlRepositoryPort;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.VisaFileControlEntity;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper.VisaFileControlMapper;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VisaFileControlRepositoryAdapter implements VisaFileControlRepositoryPort {

    private final VisaFileControlJpaRepository jpaRepository;
    private final VisaFileControlMapper mapper;

    @Override
    public VisaFileControl save(VisaFileControl fileControl) {
        VisaFileControlEntity entity = mapper.toEntity(fileControl);
        VisaFileControlEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<VisaFileControl> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
