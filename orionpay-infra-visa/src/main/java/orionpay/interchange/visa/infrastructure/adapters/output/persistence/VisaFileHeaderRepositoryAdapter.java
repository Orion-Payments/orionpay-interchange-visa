package orionpay.interchange.visa.infrastructure.adapters.output.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import orionpay.interchange.visa.domain.model.VisaFileHeader;
import orionpay.interchange.visa.domain.ports.output.VisaFileHeaderRepositoryPort;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.VisaFileHeaderEntity;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper.VisaFileHeaderMapper;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VisaFileHeaderRepositoryAdapter implements VisaFileHeaderRepositoryPort {

    private final VisaFileHeaderJpaRepository jpaRepository;
    private final VisaFileHeaderMapper mapper;

    @Override
    public VisaFileHeader save(VisaFileHeader header) {
        VisaFileHeaderEntity entity = mapper.toEntity(header);
        VisaFileHeaderEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<VisaFileHeader> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
