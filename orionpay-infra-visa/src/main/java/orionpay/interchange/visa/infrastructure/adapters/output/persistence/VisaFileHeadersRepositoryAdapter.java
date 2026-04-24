package orionpay.interchange.visa.infrastructure.adapters.output.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import orionpay.interchange.visa.domain.model.VisaFileHeaders;
import orionpay.interchange.visa.domain.ports.output.VisaFileHeadersRepositoryPort;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.VisaFileHeadersEntity;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper.VisaFileHeadersMapper;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VisaFileHeadersRepositoryAdapter implements VisaFileHeadersRepositoryPort {

    private final VisaFileHeadersJpaRepository jpaRepository;
    private final VisaFileHeadersMapper mapper;

    @Override
    public VisaFileHeaders save(VisaFileHeaders visaFileHeaders) {
        VisaFileHeadersEntity entity = mapper.toEntity(visaFileHeaders);
        VisaFileHeadersEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<VisaFileHeaders> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
