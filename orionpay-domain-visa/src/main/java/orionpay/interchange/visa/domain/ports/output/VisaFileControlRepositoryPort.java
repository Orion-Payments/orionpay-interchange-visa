package orionpay.interchange.visa.domain.ports.output;

import orionpay.interchange.visa.domain.model.VisaFileControl;
import java.util.Optional;

public interface VisaFileControlRepositoryPort {
    VisaFileControl save(VisaFileControl fileControl);
    Optional<VisaFileControl> findById(Long id);
}
