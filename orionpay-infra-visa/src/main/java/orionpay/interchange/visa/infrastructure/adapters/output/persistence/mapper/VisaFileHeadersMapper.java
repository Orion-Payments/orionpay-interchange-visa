package orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import orionpay.interchange.visa.domain.model.VisaFileHeaders;
import orionpay.interchange.visa.domain.vo.VisaAuditId;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.VisaFileHeadersEntity;

@Mapper(componentModel = "spring")
public interface VisaFileHeadersMapper {

    @Mapping(target = "auditUuid", source = "auditUuid", qualifiedByName = "unwrapVisaAuditId")
    VisaFileHeadersEntity toEntity(VisaFileHeaders domain);

    @Mapping(target = "auditUuid", source = "auditUuid", qualifiedByName = "wrapVisaAuditId")
    VisaFileHeaders toDomain(VisaFileHeadersEntity entity);

    @Named("unwrapVisaAuditId")
    default String unwrapVisaAuditId(VisaAuditId auditId) {
        return auditId != null ? auditId.getValue() : null;
    }

    @Named("wrapVisaAuditId")
    default VisaAuditId wrapVisaAuditId(String uuid) {
        return uuid != null ? new VisaAuditId(uuid) : null;
    }
}
