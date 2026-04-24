package orionpay.interchange.visa.incoming.application.dto;

import lombok.Data;

@Data
public class TC50TCR0Dto {
    private String transactionCode;
    private String destinationBin;
    private String amount; // Fixed length, needs decimal logic before converting to BigDecimal
    private String currencyCode;
    private String settlementDate;
    private String usageCode;
    private String reasonCode;
    private String rawLine; // Para enviar à auditoria (170 chars)
}
