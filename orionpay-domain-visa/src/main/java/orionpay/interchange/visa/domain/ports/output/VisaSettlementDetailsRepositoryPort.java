package orionpay.interchange.visa.domain.ports.output;

import orionpay.interchange.visa.domain.model.VisaSettlementDetails;
import java.util.Optional;

public interface VisaSettlementDetailsRepositoryPort {
    VisaSettlementDetails save(VisaSettlementDetails settlementDetails);
    Optional<VisaSettlementDetails> findById(Long id);
}
