package orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "visa_file_header", schema = "visa_interchange")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VisaFileHeaderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audit_uuid", referencedColumnName = "correlation_uuid")
    private AuditLogBaseEntity audit;

    @Column(name = "file_type_code", length = 10)
    private String fileTypeCode;

    @Column(name = "processing_bin", length = 6)
    private String processingBin;

    @Column(name = "transmission_date")
    private LocalDate transmissionDate;

    @Column(name = "unique_file_id", length = 30)
    private String uniqueFileId;

    @Column(name = "is_test_environment")
    private Boolean isTestEnvironment;

    @Column(name = "release_number", length = 3)
    private String releaseNumber;

    @Column(name = "security_code", length = 8)
    private String securityCode;
}
