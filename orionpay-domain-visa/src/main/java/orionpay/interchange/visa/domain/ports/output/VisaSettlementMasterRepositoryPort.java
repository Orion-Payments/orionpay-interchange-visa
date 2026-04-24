package orionpay.interchange.visa.domain.ports.output;

import orionpay.interchange.visa.domain.model.VisaSettlementMaster;
import java.util.Optional;

public interface VisaSettlementMasterRepositoryPort {
    VisaSettlementMaster save(VisaSettlementMaster master);
    Optional<VisaSettlementMaster> findById(Long id);
}
