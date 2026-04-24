package orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import orionpay.interchange.visa.domain.model.VisaFileSummary;
import orionpay.interchange.visa.domain.vo.MoneyAmount;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.VisaFileSummaryEntity;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface VisaFileSummaryMapper {

    @Mapping(target = "totalAggregateAmount", source = "totalAggregateAmount", qualifiedByName = "unwrapAmount")
    VisaFileSummaryEntity toEntity(VisaFileSummary domain);

    @Mapping(target = "totalAggregateAmount", expression = "java(createMoneyAmount(entity.getTotalAggregateAmount()))")
    VisaFileSummary toDomain(VisaFileSummaryEntity entity);

    @Named("unwrapAmount")
    default BigDecimal unwrapAmount(MoneyAmount moneyAmount) {
        return moneyAmount != null ? moneyAmount.value() : null;
    }

    default MoneyAmount createMoneyAmount(BigDecimal amount) {
        if (amount == null) return null;
        return new MoneyAmount(amount, "986"); // Default BRL
    }
}
