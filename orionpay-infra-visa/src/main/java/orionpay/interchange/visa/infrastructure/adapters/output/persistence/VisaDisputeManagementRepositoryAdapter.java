package orionpay.interchange.visa.infrastructure.adapters.output.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import orionpay.interchange.visa.domain.model.VisaDisputeManagement;
import orionpay.interchange.visa.domain.ports.output.VisaDisputeManagementRepositoryPort;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.VisaDisputeManagementEntity;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper.VisaDisputeManagementMapper;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VisaDisputeManagementRepositoryAdapter implements VisaDisputeManagementRepositoryPort {

    private final VisaDisputeManagementJpaRepository jpaRepository;
    private final VisaDisputeManagementMapper mapper;

    @Override
    public VisaDisputeManagement save(VisaDisputeManagement dispute) {
        VisaDisputeManagementEntity entity = mapper.toEntity(dispute);
        VisaDisputeManagementEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<VisaDisputeManagement> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
