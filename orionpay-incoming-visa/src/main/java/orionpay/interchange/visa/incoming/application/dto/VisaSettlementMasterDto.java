package orionpay.interchange.visa.incoming.application.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class VisaSettlementMasterDto {
    private Long id;
    private Long fileControlId;
    private String transactionIdOrionpay;
    private String cardBin;
    private String merchantIdVisa;
    private BigDecimal grossAmount;
    private BigDecimal interchangeAmount;
    private String currencyCode;
    private LocalDate transactionDate;
    private LocalDate settlementDate;
    private String authCode;
    private String rrnNumber;
    private String auditUuid;
    private LocalDateTime createdAt;
    private String usageCode;
    private String reasonCode;
    private String settlementFlag;
    private BigDecimal netAmount; // Valor líquido calculado pelo domínio
}
