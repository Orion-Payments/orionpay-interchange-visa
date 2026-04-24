package orionpay.interchange.visa.incoming.application.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class VisaFileHeaderDto {
    private Long id;
    private String auditUuid;
    private String fileTypeCode;
    private String processingBin;
    private LocalDate transmissionDate;
    private String uniqueFileId;
    private Boolean isTestEnvironment;
    private String releaseNumber;
    private String securityCode;
}
