package orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import orionpay.interchange.visa.domain.model.VisaDisputeManagement;
import orionpay.interchange.visa.domain.vo.MoneyAmount;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.VisaDisputeManagementEntity;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface VisaDisputeManagementMapper {

    @Mapping(target = "originalSourceAmount", source = "originalSourceAmount", qualifiedByName = "unwrapAmount")
    @Mapping(target = "originalSourceCurrency", source = "originalSourceAmount", qualifiedByName = "unwrapCurrency")
    @Mapping(target = "interchangeFeeAmount", source = "interchangeFeeAmount", qualifiedByName = "unwrapAmount")
    VisaDisputeManagementEntity toEntity(VisaDisputeManagement domain);

    @Mapping(target = "originalSourceAmount", expression = "java(createMoneyAmount(entity.getOriginalSourceAmount(), entity.getOriginalSourceCurrency()))")
    @Mapping(target = "interchangeFeeAmount", expression = "java(createMoneyAmount(entity.getInterchangeFeeAmount(), entity.getOriginalSourceCurrency()))")
    VisaDisputeManagement toDomain(VisaDisputeManagementEntity entity);

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
        return new MoneyAmount(amount, currencyCode != null ? currencyCode : "986");
    }
}
