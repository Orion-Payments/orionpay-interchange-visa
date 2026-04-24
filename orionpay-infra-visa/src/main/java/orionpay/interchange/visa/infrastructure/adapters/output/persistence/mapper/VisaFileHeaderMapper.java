package orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import orionpay.interchange.visa.domain.model.VisaFileHeader;
import orionpay.interchange.visa.domain.vo.CorrelationId;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.AuditLogBaseEntity;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.VisaFileHeaderEntity;

@Mapper(componentModel = "spring")
public interface VisaFileHeaderMapper {

    @Mapping(target = "audit.correlationUuid", source = "auditUuid", qualifiedByName = "unwrapCorrelationId")
    VisaFileHeaderEntity toEntity(VisaFileHeader domain);

    @Mapping(target = "auditUuid", source = "audit.correlationUuid", qualifiedByName = "wrapCorrelationId")
    VisaFileHeader toDomain(VisaFileHeaderEntity entity);

    @Named("unwrapCorrelationId")
    default String unwrapCorrelationId(CorrelationId correlationId) {
        return correlationId != null ? correlationId.value() : null;
    }

    @Named("wrapCorrelationId")
    default CorrelationId wrapCorrelationId(String uuid) {
        return uuid != null ? new CorrelationId(uuid) : null;
    }
}
