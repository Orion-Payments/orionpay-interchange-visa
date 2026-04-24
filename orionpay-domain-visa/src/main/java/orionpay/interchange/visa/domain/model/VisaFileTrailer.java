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
public class VisaFileTrailer {
    private Long id;
    private Long fileHeaderId;
    private String batchNumber;
    private Integer transactionCount;
    private Integer monetaryRecordCount;
    private Integer totalRecordCount;
    private MoneyAmount totalDestinationAmount;
    private String hashTotalHexa;
    private String statusName;
    private String sourceFileName;
    private LocalDate movementDate;
}
