package orionpay.interchange.visa.incoming.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import orionpay.interchange.visa.domain.model.VisaSettlementMaster;
import orionpay.interchange.visa.domain.vo.CorrelationId;
import orionpay.interchange.visa.domain.vo.MoneyAmount;
import orionpay.interchange.visa.incoming.application.dto.VisaSettlementMasterDto;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface VisaSettlementMasterAppMapper {

    @Mapping(target = "grossAmount", source = "grossAmount", qualifiedByName = "unwrapAmount")
    @Mapping(target = "currencyCode", source = "grossAmount", qualifiedByName = "unwrapCurrency")
    @Mapping(target = "interchangeAmount", source = "interchangeFee", qualifiedByName = "unwrapAmount")
    @Mapping(target = "auditUuid", source = "auditUuid", qualifiedByName = "unwrapCorrelationId")
    @Mapping(target = "netAmount", expression = "java(domain.calculateNetAmount() != null ? domain.calculateNetAmount().value() : null)")
    // Mapping the new properties
    @Mapping(target = "usageCode", source = "usageCode")
    @Mapping(target = "reasonCode", source = "reasonCode")
    VisaSettlementMasterDto toDto(VisaSettlementMaster domain);

    @Mapping(target = "grossAmount", expression = "java(createMoneyAmount(dto.getGrossAmount(), dto.getCurrencyCode()))")
    @Mapping(target = "interchangeFee", expression = "java(createMoneyAmount(dto.getInterchangeAmount(), dto.getCurrencyCode()))")
    @Mapping(target = "auditUuid", source = "auditUuid", qualifiedByName = "wrapCorrelationId")
    // Mapping the new properties
    @Mapping(target = "usageCode", source = "usageCode")
    @Mapping(target = "reasonCode", source = "reasonCode")
    VisaSettlementMaster toDomain(VisaSettlementMasterDto dto);

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
