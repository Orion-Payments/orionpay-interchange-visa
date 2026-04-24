package orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "visa_file_summary", schema = "visa_interchange")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VisaFileSummaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "header_id")
    private Long headerId;

    @Column(name = "batch_number", length = 6)
    private String batchNumber;

    @Column(name = "total_transactions_count")
    private Integer totalTransactionsCount;

    @Column(name = "monetary_records_count")
    private Integer monetaryRecordsCount;

    @Column(name = "total_tcr_rows_count")
    private Integer totalTcrRowsCount;

    @Column(name = "total_aggregate_amount", precision = 19, scale = 2)
    private BigDecimal totalAggregateAmount;

    @Column(name = "file_status_name", length = 20)
    private String fileStatusName;

    @Column(name = "movement_reference_date")
    private LocalDate movementReferenceDate;
}
