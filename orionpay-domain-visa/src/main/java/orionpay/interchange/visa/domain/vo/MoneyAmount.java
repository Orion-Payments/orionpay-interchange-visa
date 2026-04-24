package orionpay.interchange.visa.domain.vo;

import java.math.BigDecimal;

public record MoneyAmount(BigDecimal value, String currencyCode) {
    public MoneyAmount {
        if (value == null) {
            throw new IllegalArgumentException("Amount value cannot be null");
        }
        if (currencyCode == null || currencyCode.isBlank()) {
            currencyCode = "986"; // Default BRL
        }
    }

    public static MoneyAmount of(BigDecimal value) {
        return new MoneyAmount(value, "986");
    }

    public BigDecimal getAmount() {
        return value;
    }
}
