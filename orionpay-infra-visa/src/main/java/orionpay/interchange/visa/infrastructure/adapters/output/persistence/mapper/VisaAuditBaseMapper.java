package orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import orionpay.interchange.visa.domain.model.VisaAuditBase;
import orionpay.interchange.visa.domain.vo.CorrelationId;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.VisaAuditBaseEntity;

@Mapper(componentModel = "spring")
public interface VisaAuditBaseMapper {

    @Mapping(target = "uuid", source = "correlationId", qualifiedByName = "unwrapCorrelationId")
    VisaAuditBaseEntity toEntity(VisaAuditBase domain);

    @Mapping(target = "correlationId", source = "uuid", qualifiedByName = "wrapCorrelationId")
    VisaAuditBase toDomain(VisaAuditBaseEntity entity);

    @Named("unwrapCorrelationId")
    default String unwrapCorrelationId(CorrelationId correlationId) {
        return correlationId != null ? correlationId.value() : null;
    }

    @Named("wrapCorrelationId")
    default CorrelationId wrapCorrelationId(String uuid) {
        return uuid != null ? new CorrelationId(uuid) : null;
    }
}
