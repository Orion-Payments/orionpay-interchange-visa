package orionpay.interchange.visa.incoming.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import orionpay.interchange.visa.domain.model.VisaFileTrailer;
import orionpay.interchange.visa.domain.vo.MoneyAmount;
import orionpay.interchange.visa.incoming.application.dto.VisaFileTrailerDto;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface VisaFileTrailerAppMapper {

    @Mapping(target = "totalDestinationAmount", source = "totalDestinationAmount", qualifiedByName = "unwrapAmount")
    VisaFileTrailerDto toDto(VisaFileTrailer domain);

    @Mapping(target = "totalDestinationAmount", expression = "java(createMoneyAmount(dto.getTotalDestinationAmount()))")
    VisaFileTrailer toDomain(VisaFileTrailerDto dto);

    @Named("unwrapAmount")
    default BigDecimal unwrapAmount(MoneyAmount moneyAmount) {
        return moneyAmount != null ? moneyAmount.getAmount() : null;
    }

    default MoneyAmount createMoneyAmount(BigDecimal amount) {
        if (amount == null) return null;
        return new MoneyAmount(amount, "986"); // Default BRL
    }
}
