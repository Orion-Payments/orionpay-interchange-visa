package orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import orionpay.interchange.visa.domain.model.AuditLogBase;
import orionpay.interchange.visa.domain.vo.CorrelationId;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.AuditLogBaseEntity;

@Mapper(componentModel = "spring")
public interface  AuditLogBaseMapper {

//    @Mapping(target = "createdAt", source = "dateCreated")
    @Mapping(target = "correlationUuid", source = "correlationId", qualifiedByName = "unwrapCorrelationId")
    AuditLogBaseEntity toEntity(AuditLogBase domain);

    @Mapping(target = "correlationId", source = "correlationUuid", qualifiedByName = "wrapCorrelationId")
    AuditLogBase toDomain(AuditLogBaseEntity entity);

    @Named("unwrapCorrelationId")
    default String unwrapCorrelationId(CorrelationId correlationId) {
        return correlationId != null ? correlationId.value() : null;
    }

    @Named("wrapCorrelationId")
    default CorrelationId wrapCorrelationId(String uuid) {
        return uuid != null ? new CorrelationId(uuid) : null;
    }
}
