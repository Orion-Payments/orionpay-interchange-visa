package orionpay.interchange.visa.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import orionpay.interchange.visa.domain.vo.CorrelationId;
import orionpay.interchange.visa.domain.vo.MoneyAmount;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisaFileControl {
    
    private Long id;
    private CorrelationId auditUuid;
    private String fileName;
    private String fileTypeIndicator;
    private String sourceBin;
    private LocalDate transmissionDate;
    private Integer totalRecordCount;
    private MoneyAmount totalAmount;
    private String statusName;
    private LocalDate controlMovementDate;

    public void updateStatus(String newStatus) {
        this.statusName = newStatus;
    }

    /**
     * Confirma a integridade do arquivo cruzando os valores calculados internamente com os valores informados no Trailer (TCR92).
     */
    public void confirmIntegrity(BigDecimal expectedAmount, Integer expectedCount) {
        boolean isAmountValid = this.totalAmount != null && expectedAmount != null && 
                                this.totalAmount.value().compareTo(expectedAmount) == 0;
                                
        boolean isCountValid = this.totalRecordCount != null && expectedCount != null && 
                               this.totalRecordCount.equals(expectedCount);

        if (isAmountValid && isCountValid) {
            this.statusName = "COMPLETED";
        } else {
            this.statusName = "FAILED_INTEGRITY";
            throw new IllegalStateException(String.format(
                "Falha na integridade do arquivo. Esperado: [Valor: %s, Qtd: %d]. Calculado: [Valor: %s, Qtd: %d]",
                expectedAmount, expectedCount, 
                this.totalAmount != null ? this.totalAmount.value() : null, 
                this.totalRecordCount));
        }
    }
}
