package orionpay.interchange.visa.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import orionpay.interchange.visa.domain.vo.CorrelationId;

import java.time.LocalDateTime;

/**
 * Domain Model para a tabela visa_interchange.audit_log_base.
 * Aplicando POO/SOLID: Evita a anemia delegando comportamentos de mudança de status a métodos específicos.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogBase {

    private CorrelationId correlationId;
    private String rawLineContent;
    private Integer processingStatusId;
    private String statusMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Atualiza o status do processamento (ex: de IN_PROGRESS para SUCCESS).
     */
    public void markAsProcessed(String message) {
        this.processingStatusId = 2; // Supondo q 2 = Sucesso
        this.statusMessage = message;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsFailed(String errorMessage) {
        this.processingStatusId = 3; // Supondo q 3 = Erro
        this.statusMessage = errorMessage;
        this.updatedAt = LocalDateTime.now();
    }
}
