package orionpay.interchange.visa.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import orionpay.interchange.visa.domain.vo.MoneyAmount;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisaFileSummary {
    private Long id;
    private Long headerId;
    private String batchNumber;
    private Integer totalTransactionsCount;
    private Integer monetaryRecordsCount;
    private Integer totalTcrRowsCount;
    private MoneyAmount totalAggregateAmount;
    private String fileStatusName;
    private LocalDate movementReferenceDate;
}
