package orionpay.interchange.visa.incoming.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import orionpay.interchange.visa.domain.ports.output.AuditLogBaseRepositoryPort;
import orionpay.interchange.visa.domain.ports.output.InternalTransactionPort;
import orionpay.interchange.visa.domain.ports.output.VisaReconciliationHistoryPort;
import orionpay.interchange.visa.domain.ports.output.VisaSettlementRepositoryPort;
import orionpay.interchange.visa.domain.service.FinancialReconciliationService;
import orionpay.interchange.visa.domain.usecase.ProcessVisaSettlementUseCase;

@Configuration
public class UseCaseConfig {

    @Bean
    public ProcessVisaSettlementUseCase processVisaSettlementUseCase(
            VisaSettlementRepositoryPort settlementRepositoryPort,
            AuditLogBaseRepositoryPort auditLogBaseRepositoryPort) {
        return new ProcessVisaSettlementUseCase(settlementRepositoryPort, auditLogBaseRepositoryPort);
    }

    @Bean
    public FinancialReconciliationService financialReconciliationService(
            VisaSettlementRepositoryPort settlementRepositoryPort,
            InternalTransactionPort internalTransactionPort,
            VisaReconciliationHistoryPort reconciliationHistoryPort) {
        return new FinancialReconciliationService(settlementRepositoryPort, internalTransactionPort, reconciliationHistoryPort);
    }
}
