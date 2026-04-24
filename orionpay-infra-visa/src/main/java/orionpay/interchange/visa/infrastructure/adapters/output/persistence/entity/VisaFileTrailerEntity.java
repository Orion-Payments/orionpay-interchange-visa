package orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "visa_file_trailers", schema = "visa_interchange")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VisaFileTrailerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_header_id")
    private Long fileHeaderId;

    @Column(name = "batch_number", length = 6)
    private String batchNumber;

    @Column(name = "transaction_count")
    private Integer transactionCount;

    @Column(name = "monetary_record_count")
    private Integer monetaryRecordCount;

    @Column(name = "total_record_count")
    private Integer totalRecordCount;

    @Column(name = "total_destination_amount", precision = 19, scale = 2)
    private BigDecimal totalDestinationAmount;

    @Column(name = "hash_total_hexa", length = 4)
    private String hashTotalHexa;

    @Column(name = "status_name", length = 20)
    private String statusName;

    @Column(name = "source_file_name", length = 255)
    private String sourceFileName;

    @Column(name = "movement_date")
    private LocalDate movementDate;
}
