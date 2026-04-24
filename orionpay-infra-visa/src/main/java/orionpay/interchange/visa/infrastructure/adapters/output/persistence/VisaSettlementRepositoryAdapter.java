package orionpay.interchange.visa.infrastructure.adapters.output.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import orionpay.interchange.visa.domain.model.VisaSettlementMaster;
import orionpay.interchange.visa.domain.ports.output.VisaSettlementRepositoryPort;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.VisaSettlementMasterEntity;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper.VisaSettlementMasterMapper;

@Component
@RequiredArgsConstructor
public class VisaSettlementRepositoryAdapter implements VisaSettlementRepositoryPort {

    private final VisaSettlementMasterJpaRepository jpaRepository;
    private final VisaSettlementMasterMapper mapper;

    @Override
    public VisaSettlementMaster save(VisaSettlementMaster settlement) {
        // 1. Mapeia o Domain Model rico para a Entity anêmica do JPA
        VisaSettlementMasterEntity entity = mapper.toEntity(settlement);
        
        // 2. Persiste usando o Spring Data
        VisaSettlementMasterEntity savedEntity = jpaRepository.save(entity);
        
        // 3. Mapeia a Entity salva de volta para o Domain Model
        return mapper.toDomain(savedEntity);
    }
}
