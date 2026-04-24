package orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import orionpay.interchange.visa.domain.model.VisaNationalReversalDetails;
import orionpay.interchange.visa.domain.vo.MoneyAmount;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.VisaNationalReversalDetailsEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface VisaNationalReversalDetailsMapper {

    @Mapping(target = "nationalReimbursementFee", source = "nationalReimbursementFee", qualifiedByName = "unwrapAmount")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "toOffsetDateTime")
    VisaNationalReversalDetailsEntity toEntity(VisaNationalReversalDetails domain);

    @Mapping(target = "nationalReimbursementFee", expression = "java(createMoneyAmount(entity.getNationalReimbursementFee()))")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "toLocalDateTime")
    VisaNationalReversalDetails toDomain(VisaNationalReversalDetailsEntity entity);

    @Named("unwrapAmount")
    default BigDecimal unwrapAmount(MoneyAmount moneyAmount) {
        return moneyAmount != null ? moneyAmount.value() : null;
    }

    default MoneyAmount createMoneyAmount(BigDecimal amount) {
        if (amount == null) return null;
        return new MoneyAmount(amount, "986");
    }

    @Named("toOffsetDateTime")
    default OffsetDateTime toOffsetDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return localDateTime.atOffset(ZoneOffset.UTC); // Ou seu ZoneOffset preferido
    }

    @Named("toLocalDateTime")
    default LocalDateTime toLocalDateTime(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) return null;
        return offsetDateTime.toLocalDateTime();
    }
}
