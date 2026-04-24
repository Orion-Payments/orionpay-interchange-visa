package orionpay.interchange.visa.incoming.application.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class VisaFileSummaryDto {
    private Long id;
    private Long headerId;
    private String batchNumber;
    private Integer totalTransactionsCount;
    private Integer monetaryRecordsCount;
    private Integer totalTcrRowsCount;
    private BigDecimal totalAggregateAmount;
    private String fileStatusName;
    private LocalDate movementReferenceDate;
}
