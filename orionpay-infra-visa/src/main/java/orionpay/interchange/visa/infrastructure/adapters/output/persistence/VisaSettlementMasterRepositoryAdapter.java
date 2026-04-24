package orionpay.interchange.visa.infrastructure.adapters.output.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import orionpay.interchange.visa.domain.model.VisaSettlementMaster;
import orionpay.interchange.visa.domain.ports.output.VisaSettlementMasterRepositoryPort;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.VisaSettlementMasterEntity;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper.VisaSettlementMasterMapper;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VisaSettlementMasterRepositoryAdapter implements VisaSettlementMasterRepositoryPort {

    private final VisaSettlementMasterJpaRepository jpaRepository;
    private final VisaSettlementMasterMapper mapper;

    @Override
    public VisaSettlementMaster save(VisaSettlementMaster master) {
        VisaSettlementMasterEntity entity = mapper.toEntity(master);
        VisaSettlementMasterEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<VisaSettlementMaster> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
