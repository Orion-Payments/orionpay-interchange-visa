package orionpay.interchange.visa.domain.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orionpay.interchange.visa.domain.model.InternalTransaction;
import orionpay.interchange.visa.domain.model.VisaReconciliationHistory;
import orionpay.interchange.visa.domain.model.VisaSettlementMaster;
import orionpay.interchange.visa.domain.ports.output.InternalTransactionPort;
import orionpay.interchange.visa.domain.ports.output.VisaReconciliationHistoryPort;
import orionpay.interchange.visa.domain.ports.output.VisaSettlementRepositoryPort;
import orionpay.interchange.visa.domain.vo.ReconciliationStatus;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Domain Service que coordena a conciliação financeira entre a Visa e as Autorizações Internas.
 */
@RequiredArgsConstructor
public class FinancialReconciliationService {

    private static final Logger log = LoggerFactory.getLogger(FinancialReconciliationService.class);

    private final VisaSettlementRepositoryPort settlementRepositoryPort;
    private final InternalTransactionPort internalTransactionPort;
    private final VisaReconciliationHistoryPort reconciliationHistoryPort;

    public VisaSettlementMaster reconcile(VisaSettlementMaster settlement) {
        ReconciliationStatus oldStatus = settlement.getReconciliationStatus();
        String rrn = settlement.getRrnNumber();
        String authCode = settlement.getAuthCode();

        // 1. Regra de Negócio: Não conciliar sem chaves
        if (rrn == null || rrn.isBlank() || authCode == null || authCode.isBlank()) {
            log.warn("Tentativa de conciliação sem RRN ou AuthCode. Settlement ID: {}", settlement.getId());
            return settlement; // Mantém PENDING
        }

        // 2. Busca na Porta Externa (Pode ser API de outro microsserviço ou BD Interno)
        Optional<InternalTransaction> internalTxOpt = internalTransactionPort.findByRrnAndAuthCode(rrn, authCode);

        ReconciliationStatus newStatus;
        String historyReason;

        if (internalTxOpt.isEmpty()) {
            // Regra: Venda liquidada pela Visa, mas desconhecida internamente.
            newStatus = ReconciliationStatus.UNMATCHED_VISA;
            historyReason = "Transação não encontrada na base de autorização interna.";
        } else {
            InternalTransaction internalTx = internalTxOpt.get();
            
            // Regra: Compara o valor bruto da liquidação (Gross) com o valor autorizado.
            if (settlement.getGrossAmount().value().compareTo(internalTx.getAuthorizedAmount().value()) == 0) {
                newStatus = ReconciliationStatus.MATCHED;
                historyReason = "Valores coincidem com a transação interna autorizada.";
            } else {
                // Regra: O valor liquidado é diferente do autorizado.
                newStatus = ReconciliationStatus.DISPUTED;
                historyReason = String.format("Divergência de valor. Visa: %s, Interno: %s", 
                        settlement.getGrossAmount().value(), internalTx.getAuthorizedAmount().value());
            }
        }

        // 3. Aplica a mudança de estado e salva
        if (oldStatus != newStatus) {
            settlement.changeReconciliationStatus(newStatus);
            VisaSettlementMaster savedSettlement = settlementRepositoryPort.save(settlement);

            // 4. Cria o histórico de auditoria do Caixa
            VisaReconciliationHistory history = VisaReconciliationHistory.builder()
                    .settlementMasterId(savedSettlement.getId())
                    .oldStatus(oldStatus)
                    .newStatus(newStatus)
                    .reason(historyReason)
                    .changedAt(LocalDateTime.now())
                    .build();
            reconciliationHistoryPort.save(history);

            log.info("Conciliação finalizada para Settlement ID {}. De {} para {}", 
                    savedSettlement.getId(), oldStatus, newStatus);
            return savedSettlement;
        }

        return settlement;
    }
}
