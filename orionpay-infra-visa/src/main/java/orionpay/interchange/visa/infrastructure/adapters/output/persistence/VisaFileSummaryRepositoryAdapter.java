package orionpay.interchange.visa.infrastructure.adapters.output.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import orionpay.interchange.visa.domain.model.VisaFileSummary;
import orionpay.interchange.visa.domain.ports.output.VisaFileSummaryRepositoryPort;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.VisaFileSummaryEntity;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper.VisaFileSummaryMapper;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VisaFileSummaryRepositoryAdapter implements VisaFileSummaryRepositoryPort {

    private final VisaFileSummaryJpaRepository jpaRepository;
    private final VisaFileSummaryMapper mapper;

    @Override
    public VisaFileSummary save(VisaFileSummary summary) {
        VisaFileSummaryEntity entity = mapper.toEntity(summary);
        VisaFileSummaryEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<VisaFileSummary> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
