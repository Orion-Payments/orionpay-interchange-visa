package orionpay.interchange.visa.application.mapper;

import orionpay.interchange.visa.domain.model.AuditLogBase;
import orionpay.interchange.visa.domain.vo.CorrelationId;
import orionpay.interchange.visa.application.dto.AuditLogBaseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring", uses = {CorrelationIdDtoMapper.class})
public interface AuditLogBaseDtoMapper {

    @Mapping(source = "correlationId", target = "correlationId", qualifiedByName = "mapCorrelationIdToString")
    AuditLogBaseDTO toDto(AuditLogBase domain);

    @Mapping(source = "correlationId", target = "correlationId", qualifiedByName = "mapStringToCorrelationId")
    AuditLogBase toDomain(AuditLogBaseDTO dto);


    default OffsetDateTime map(LocalDateTime value) {
        return value == null ? null : value.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }

    default LocalDateTime map(OffsetDateTime value) {
        return value == null ? null : value.toLocalDateTime();
    }
}
