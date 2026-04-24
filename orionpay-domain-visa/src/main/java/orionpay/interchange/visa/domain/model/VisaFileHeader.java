package orionpay.interchange.visa.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import orionpay.interchange.visa.domain.vo.CorrelationId;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisaFileHeader {
    private Long id;
    private CorrelationId auditUuid;
    private String fileTypeCode;
    private String processingBin;
    private LocalDate transmissionDate;
    private String uniqueFileId;
    private Boolean isTestEnvironment;
    private String releaseNumber;
    private String securityCode;
}
