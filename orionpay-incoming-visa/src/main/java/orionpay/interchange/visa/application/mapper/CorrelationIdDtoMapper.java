package orionpay.interchange.visa.application.mapper;

import orionpay.interchange.visa.domain.vo.CorrelationId;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public class CorrelationIdDtoMapper {

    @Named("mapStringToCorrelationId")
    public CorrelationId map(String uuid) {
        return uuid != null ? new CorrelationId(uuid) : null;
    }

    @Named("mapCorrelationIdToString")
    public String map(CorrelationId correlationId) {
        return correlationId != null ? correlationId.value() : null;
    }
}
