package orionpay.interchange.visa.infrastructure.adapters.output.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import orionpay.interchange.visa.domain.model.VisaFileControl;
import orionpay.interchange.visa.domain.vo.CorrelationId;
import orionpay.interchange.visa.domain.vo.MoneyAmount;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.AuditLogBaseEntity;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.VisaFileControlEntity;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface VisaFileControlMapper {

    @Mapping(target = "audit.correlationUuid", source = "auditUuid", qualifiedByName = "unwrapCorrelationId")
    @Mapping(target = "totalAmount", source = "totalAmount", qualifiedByName = "unwrapAmount")
    VisaFileControlEntity toEntity(VisaFileControl domain);

    @Mapping(target = "auditUuid", source = "audit.correlationUuid", qualifiedByName = "wrapCorrelationId")
    @Mapping(target = "totalAmount", expression = "java(createMoneyAmount(entity.getTotalAmount()))")
    VisaFileControl toDomain(VisaFileControlEntity entity);

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

    default MoneyAmount createMoneyAmount(BigDecimal amount) {
        if (amount == null) return null;
        // Assumindo BRL como padrão, idealmente a entidade teria o currency code
        return new MoneyAmount(amount, "986");
    }
}
