package orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import orionpay.interchange.visa.domain.model.VisaClearingPresentation;
import orionpay.interchange.visa.domain.vo.MoneyAmount;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.VisaClearingPresentationEntity;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface VisaClearingPresentationMapper {

    @Mapping(target = "nationalReimbursementFee", source = "nationalReimbursementFee", qualifiedByName = "unwrapAmount")
    @Mapping(target = "cryptogramAmount", source = "cryptogramAmount", qualifiedByName = "unwrapAmount")
    VisaClearingPresentationEntity toEntity(VisaClearingPresentation domain);

    @Mapping(target = "nationalReimbursementFee", expression = "java(createMoneyAmount(entity.getNationalReimbursementFee()))")
    @Mapping(target = "cryptogramAmount", expression = "java(createMoneyAmount(entity.getCryptogramAmount()))")
    VisaClearingPresentation toDomain(VisaClearingPresentationEntity entity);

    @Named("unwrapAmount")
    default BigDecimal unwrapAmount(MoneyAmount moneyAmount) {
        return moneyAmount != null ? moneyAmount.value() : null;
    }

    default MoneyAmount createMoneyAmount(BigDecimal amount) {
        if (amount == null) return null;
        return new MoneyAmount(amount, "986");
    }
}
