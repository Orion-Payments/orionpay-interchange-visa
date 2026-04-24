package orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import orionpay.interchange.visa.domain.model.VisaSettlementMaster;
import orionpay.interchange.visa.domain.vo.CorrelationId;
import orionpay.interchange.visa.domain.vo.MoneyAmount;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.VisaSettlementMasterEntity;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface VisaSettlementMasterMapper {

    @Mapping(target = "amountGross", source = "grossAmount", qualifiedByName = "unwrapAmount")
    @Mapping(target = "currencyCode", source = "grossAmount", qualifiedByName = "unwrapCurrency")
    @Mapping(target = "amountInterchange", source = "interchangeFee", qualifiedByName = "unwrapAmount")
    @Mapping(target = "auditUuid", source = "auditUuid", qualifiedByName = "unwrapCorrelationId")
    @Mapping(target = "merchantIdVisa", ignore = true)
    @Mapping(target = "transactionDate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "settlementFlag", ignore = true)
    VisaSettlementMasterEntity toEntity(VisaSettlementMaster domain);

    @Mapping(target = "grossAmount", expression = "java(createMoneyAmount(entity.getAmountGross(), entity.getCurrencyCode()))")
    @Mapping(target = "interchangeFee", expression = "java(createMoneyAmount(entity.getAmountInterchange(), entity.getCurrencyCode()))")
    @Mapping(target = "auditUuid", source = "auditUuid", qualifiedByName = "wrapCorrelationId")
    VisaSettlementMaster toDomain(VisaSettlementMasterEntity entity);

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
        return moneyAmount != null ? moneyAmount.value() : null;
    }

    @Named("unwrapCurrency")
    default String unwrapCurrency(MoneyAmount moneyAmount) {
        return moneyAmount != null ? moneyAmount.currencyCode() : null;
    }

    default MoneyAmount createMoneyAmount(BigDecimal amount, String currencyCode) {
        if (amount == null) return null;
        return new MoneyAmount(amount, currencyCode);
    }
}
