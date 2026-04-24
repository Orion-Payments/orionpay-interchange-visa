package orionpay.interchange.visa.incoming.application.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class VisaFileControlDto {
    private Long id;
    private String auditUuid;
    private String fileName;
    private String fileTypeIndicator;
    private String sourceBin;
    private LocalDate transmissionDate;
    private Integer totalRecordCount;
    private BigDecimal totalAmount;
    private String statusName;
    private LocalDate controlMovementDate;
}
