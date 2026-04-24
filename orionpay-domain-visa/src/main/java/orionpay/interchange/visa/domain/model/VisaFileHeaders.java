package orionpay.interchange.visa.domain.model;

import lombok.*;
import orionpay.interchange.visa.domain.vo.VisaAuditId;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisaFileHeaders {
    private Long id;
    private VisaAuditId auditUuid;
    private String fileTypeLabel;
    private String processingBin;
    private LocalDate transmissionDate;
    private String uniqueFileId;
    private String testOptionFlag;
    private String securityCode;
    private String deliveryCode;
    private String releaseNumber;
}
