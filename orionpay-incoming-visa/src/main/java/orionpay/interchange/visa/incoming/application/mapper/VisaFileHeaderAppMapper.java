package orionpay.interchange.visa.incoming.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import orionpay.interchange.visa.domain.model.VisaFileHeader;
import orionpay.interchange.visa.domain.vo.CorrelationId;
import orionpay.interchange.visa.incoming.application.dto.VisaFileHeaderDto;

@Mapper(componentModel = "spring")
public interface VisaFileHeaderAppMapper {

    @Mapping(target = "auditUuid", source = "auditUuid", qualifiedByName = "unwrapCorrelationId")
    VisaFileHeaderDto toDto(VisaFileHeader domain);

    @Mapping(target = "auditUuid", source = "auditUuid", qualifiedByName = "wrapCorrelationId")
    VisaFileHeader toDomain(VisaFileHeaderDto dto);

    @Named("unwrapCorrelationId")
    default String unwrapCorrelationId(CorrelationId correlationId) {
        return correlationId != null ? correlationId.value() : null;
    }

    @Named("wrapCorrelationId")
    default CorrelationId wrapCorrelationId(String uuid) {
        return uuid != null ? new CorrelationId(uuid) : null;
    }
}
