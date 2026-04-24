package orionpay.interchange.visa.incoming.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import orionpay.interchange.visa.domain.model.VisaFileSummary;
import orionpay.interchange.visa.domain.vo.MoneyAmount;
import orionpay.interchange.visa.incoming.application.dto.VisaFileSummaryDto;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface VisaFileSummaryAppMapper {

    @Mapping(target = "totalAggregateAmount", source = "totalAggregateAmount", qualifiedByName = "unwrapAmount")
    VisaFileSummaryDto toDto(VisaFileSummary domain);

    @Mapping(target = "totalAggregateAmount", expression = "java(createMoneyAmount(dto.getTotalAggregateAmount()))")
    VisaFileSummary toDomain(VisaFileSummaryDto dto);

    @Named("unwrapAmount")
    default BigDecimal unwrapAmount(MoneyAmount moneyAmount) {
        return moneyAmount != null ? moneyAmount.getAmount() : null;
    }

    default MoneyAmount createMoneyAmount(BigDecimal amount) {
        if (amount == null) return null;
        return new MoneyAmount(amount, "986"); // Default BRL
    }
}
