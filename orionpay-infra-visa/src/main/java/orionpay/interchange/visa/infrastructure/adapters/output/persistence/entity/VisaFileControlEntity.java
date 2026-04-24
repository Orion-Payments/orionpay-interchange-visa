package orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "visa_file_control", schema = "visa_interchange")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VisaFileControlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "audit_uuid", referencedColumnName = "correlation_uuid")
    private AuditLogBaseEntity audit;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "file_type_indicator", length = 10)
    private String fileTypeIndicator;

    @Column(name = "source_bin", length = 6)
    private String sourceBin;

    @Column(name = "transmission_date")
    private LocalDate transmissionDate;

    @Column(name = "total_record_count")
    private Integer totalRecordCount;

    @Column(name = "total_amount", precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "status_name", length = 20)
    private String statusName;

    @Column(name = "control_movement_date")
    private LocalDate controlMovementDate;
}
