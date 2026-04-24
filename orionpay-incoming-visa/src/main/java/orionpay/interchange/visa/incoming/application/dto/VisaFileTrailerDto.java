package orionpay.interchange.visa.incoming.application.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class VisaFileTrailerDto {
    private Long id;
    private Long fileHeaderId;
    private String batchNumber;
    private Integer transactionCount;
    private Integer monetaryRecordCount;
    private Integer totalRecordCount;
    private BigDecimal totalDestinationAmount;
    private String hashTotalHexa;
    private String statusName;
    private String sourceFileName;
    private LocalDate movementDate;
}
