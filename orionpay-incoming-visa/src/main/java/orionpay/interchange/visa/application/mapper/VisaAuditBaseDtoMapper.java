package orionpay.interchange.visa.application.mapper;

import org.mapstruct.Builder;
import orionpay.interchange.visa.domain.model.VisaAuditBase;
import orionpay.interchange.visa.domain.vo.VisaAuditId;
import orionpay.interchange.visa.application.dto.VisaAuditBaseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import orionpay.interchange.visa.domain.model.VisaAuditBase;
import orionpay.interchange.visa.domain.vo.CorrelationId;
import orionpay.interchange.visa.application.dto.VisaAuditBaseDTO;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Mapper(componentModel = "spring")
public interface VisaAuditBaseDtoMapper {

    @Mapping(target = "correlationId", source = "correlationId")
    VisaAuditBase toDomain(VisaAuditBaseDTO dto);

    // Método para converter String do DTO para o Record CorrelationId do Domínio
    default CorrelationId mapToCorrelationId(String value) {
        return value != null ? new CorrelationId(value) : null;
    }

    // Métodos para resolver o conflito de OffsetDateTime vs LocalDateTime
    default LocalDateTime map(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toLocalDateTime();
    }

    default OffsetDateTime map(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.atZone(java.time.ZoneId.systemDefault()).toOffsetDateTime();
    }
}