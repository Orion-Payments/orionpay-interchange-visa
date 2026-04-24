package orionpay.interchange.visa.domain.ports.output;

import orionpay.interchange.visa.domain.model.VisaFileTrailer;
import java.util.Optional;

public interface VisaFileTrailerRepositoryPort {
    VisaFileTrailer save(VisaFileTrailer trailer);
    Optional<VisaFileTrailer> findById(Long id);
}
