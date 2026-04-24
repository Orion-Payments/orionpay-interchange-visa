package orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "visa_file_headers", schema = "visa_interchange")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisaFileHeadersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "audit_uuid", length = 36)
    private String auditUuid;

    @Column(name = "file_type_label", length = 10)
    private String fileTypeLabel;

    @Column(name = "processing_bin", length = 6)
    private String processingBin;

    @Column(name = "transmission_date")
    private LocalDate transmissionDate;

    @Column(name = "unique_file_id", length = 30)
    private String uniqueFileId;

    @Column(name = "test_option_flag", length = 4)
    private String testOptionFlag;

    @Column(name = "security_code", length = 8)
    private String securityCode;

    @Column(name = "delivery_code", length = 1)
    private String deliveryCode;

    @Column(name = "release_number", length = 3)
    private String releaseNumber;
}
