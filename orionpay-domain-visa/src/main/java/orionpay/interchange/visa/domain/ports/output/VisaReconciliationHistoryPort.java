package orionpay.interchange.visa.domain.ports.output;

import orionpay.interchange.visa.domain.model.VisaReconciliationHistory;

public interface VisaReconciliationHistoryPort {
    VisaReconciliationHistory save(VisaReconciliationHistory history);
}
