package orionpay.interchange.visa.incoming.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import orionpay.interchange.visa.domain.model.VisaFileControl;
import orionpay.interchange.visa.domain.vo.CorrelationId;
import orionpay.interchange.visa.domain.vo.MoneyAmount;
import orionpay.interchange.visa.incoming.application.dto.VisaFileControlDto;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface VisaFileControlAppMapper {

    @Mapping(target = "auditUuid", source = "auditUuid", qualifiedByName = "unwrapCorrelationId")
    @Mapping(target = "totalAmount", source = "totalAmount", qualifiedByName = "unwrapAmount")
    VisaFileControlDto toDto(VisaFileControl domain);

    @Mapping(target = "auditUuid", source = "auditUuid", qualifiedByName = "wrapCorrelationId")
    @Mapping(target = "totalAmount", expression = "java(createMoneyAmount(dto.getTotalAmount()))")
    VisaFileControl toDomain(VisaFileControlDto dto);

    @Named("unwrapCorrelationId")
    default String unwrapCorrelationId(CorrelationId correlationId) {
        return correlationId != null ? correlationId.value() : null;
    }

    @Named("wrapCorrelationId")
    default CorrelationId wrapCorrelationId(String uuid) {
        return uuid != null ? new CorrelationId(uuid) : null;
    }

    @Named("unwrapAmount")
    default BigDecimal unwrapAmount(MoneyAmount moneyAmount) {
        return moneyAmount != null ? moneyAmount.getAmount() : null;
    }

    default MoneyAmount createMoneyAmount(BigDecimal amount) {
        if (amount == null) return null;
        return new MoneyAmount(amount, "986"); // Default BRL
    }
}
