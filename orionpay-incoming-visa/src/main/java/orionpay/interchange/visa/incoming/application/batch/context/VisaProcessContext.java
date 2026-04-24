package orionpay.interchange.visa.incoming.application.batch.context;

import lombok.Getter;
import lombok.Setter;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Mantém o estado do ficheiro e do UUID de correlação durante a execução do Job do Spring Batch.
 */
@Component
@JobScope
@Getter
@Setter
public class VisaProcessContext {

    private String correlationId;
    private String fileName;
    private Long fileControlId;
    private LocalDateTime processStartTime;

    // Acumuladores de totais
    private AtomicInteger totalRecordCount = new AtomicInteger(0);
    private BigDecimal totalAmount = BigDecimal.ZERO;

    public VisaProcessContext() {
        this.processStartTime = LocalDateTime.now();
        this.totalRecordCount = new AtomicInteger(0);
        this.totalAmount = BigDecimal.ZERO;
    }

    public void incrementTotalRecordCount() {
        this.totalRecordCount.incrementAndGet();
    }

    public synchronized void addTotalAmount(BigDecimal amount) {
        if (amount != null) {
            this.totalAmount = this.totalAmount.add(amount);
        }
    }
}
