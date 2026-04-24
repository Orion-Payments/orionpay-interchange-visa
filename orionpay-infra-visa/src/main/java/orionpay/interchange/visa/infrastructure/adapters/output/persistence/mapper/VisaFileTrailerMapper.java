package orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import orionpay.interchange.visa.domain.model.VisaFileTrailer;
import orionpay.interchange.visa.domain.vo.MoneyAmount;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.VisaFileTrailerEntity;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface VisaFileTrailerMapper {

    @Mapping(target = "totalDestinationAmount", source = "totalDestinationAmount", qualifiedByName = "unwrapAmount")
    VisaFileTrailerEntity toEntity(VisaFileTrailer domain);

    @Mapping(target = "totalDestinationAmount", expression = "java(createMoneyAmount(entity.getTotalDestinationAmount()))")
    VisaFileTrailer toDomain(VisaFileTrailerEntity entity);

    @Named("unwrapAmount")
    default BigDecimal unwrapAmount(MoneyAmount moneyAmount) {
        return moneyAmount != null ? moneyAmount.value() : null;
    }

    default MoneyAmount createMoneyAmount(BigDecimal amount) {
        if (amount == null) return null;
        return MoneyAmount.of(amount); // Default BRL
    }
}
