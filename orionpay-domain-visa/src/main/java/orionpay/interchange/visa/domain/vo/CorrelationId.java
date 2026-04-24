package orionpay.interchange.visa.domain.vo;

public record CorrelationId(String value) {
    public CorrelationId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Correlation ID cannot be null or empty");
        }
    }
}
