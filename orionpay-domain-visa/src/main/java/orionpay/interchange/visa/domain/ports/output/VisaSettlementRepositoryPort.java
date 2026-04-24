package orionpay.interchange.visa.domain.ports.output;

import orionpay.interchange.visa.domain.model.VisaSettlementMaster;

public interface VisaSettlementRepositoryPort {
    VisaSettlementMaster save(VisaSettlementMaster settlement);
}
