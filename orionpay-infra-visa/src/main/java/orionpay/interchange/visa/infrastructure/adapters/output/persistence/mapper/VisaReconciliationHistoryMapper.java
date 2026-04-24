package orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper;

import org.mapstruct.Mapper;
import orionpay.interchange.visa.domain.model.VisaReconciliationHistory;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.VisaReconciliationHistoryEntity;

@Mapper(componentModel = "spring")
public interface VisaReconciliationHistoryMapper {
    VisaReconciliationHistoryEntity toEntity(VisaReconciliationHistory domain);
    VisaReconciliationHistory toDomain(VisaReconciliationHistoryEntity entity);
}
