package orionpay.interchange.visa.incoming.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import orionpay.interchange.visa.domain.model.VisaAuditBase;
import orionpay.interchange.visa.domain.vo.CorrelationId;
import orionpay.interchange.visa.incoming.application.dto.VisaAuditBaseDto;

@Mapper(componentModel = "spring")
public interface VisaAuditBaseAppMapper {

    @Mapping(target = "correlationId", source = "correlationId", qualifiedByName = "unwrapCorrelationId")
    VisaAuditBaseDto toDto(VisaAuditBase domain);

    @Mapping(target = "correlationId", source = "correlationId", qualifiedByName = "wrapCorrelationId")
    VisaAuditBase toDomain(VisaAuditBaseDto dto);

    @Named("unwrapCorrelationId")
    default String unwrapCorrelationId(CorrelationId correlationId) {
        return correlationId != null ? correlationId.value() : null;
    }

    @Named("wrapCorrelationId")
    default CorrelationId wrapCorrelationId(String uuid) {
        return uuid != null ? new CorrelationId(uuid) : null;
    }
}
