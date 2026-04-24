package orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper;

import org.mapstruct.Mapper;
import orionpay.interchange.visa.domain.vo.CorrelationId;

@Mapper(componentModel = "spring")
public class CorrelationIdMapper {
    public String map(CorrelationId value) {
        if (value == null) {
            return null;
        }
        return value.value();
    }

    public CorrelationId map(String value) {
        if (value == null) {
            return null;
        }
        return new CorrelationId(value);
    }
}
