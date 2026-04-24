package orionpay.interchange.visa.domain.ports.output;

import orionpay.interchange.visa.domain.model.VisaFileHeader;
import java.util.Optional;

public interface VisaFileHeaderRepositoryPort {
    VisaFileHeader save(VisaFileHeader header);
    Optional<VisaFileHeader> findById(Long id);
}
