package orionpay.interchange.visa.domain.ports.output;

import orionpay.interchange.visa.domain.model.VisaFileHeaders;
import java.util.Optional;

public interface VisaFileHeadersRepositoryPort {
    VisaFileHeaders save(VisaFileHeaders visaFileHeaders);
    Optional<VisaFileHeaders> findById(Long id);
}
