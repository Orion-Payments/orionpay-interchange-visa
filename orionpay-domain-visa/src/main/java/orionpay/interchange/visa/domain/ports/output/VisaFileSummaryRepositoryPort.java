package orionpay.interchange.visa.domain.ports.output;

import orionpay.interchange.visa.domain.model.VisaFileSummary;
import java.util.Optional;

public interface VisaFileSummaryRepositoryPort {
    VisaFileSummary save(VisaFileSummary summary);
    Optional<VisaFileSummary> findById(Long id);
}
