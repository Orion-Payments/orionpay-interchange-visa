package orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper;

import org.mapstruct.Mapper;
import orionpay.interchange.visa.domain.model.VisaSettlementDetails;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.VisaSettlementDetailsEntity;

@Mapper(componentModel = "spring")
public interface VisaSettlementDetailsMapper {
    VisaSettlementDetailsEntity toEntity(VisaSettlementDetails domain);
    VisaSettlementDetails toDomain(VisaSettlementDetailsEntity entity);
}
