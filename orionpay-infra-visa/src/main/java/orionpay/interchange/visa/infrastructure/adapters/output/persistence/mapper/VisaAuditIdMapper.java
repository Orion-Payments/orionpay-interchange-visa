package orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper;

import orionpay.interchange.visa.domain.vo.VisaAuditId;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public class VisaAuditIdMapper {

    @Named("mapStringToVisaAuditId")
    public VisaAuditId map(String uuid) {
        return uuid != null ? new VisaAuditId(uuid) : null;
    }

    @Named("mapVisaAuditIdToString")
    public String map(VisaAuditId visaAuditId) {
        return visaAuditId != null ? visaAuditId.getValue() : null;
    }
}
