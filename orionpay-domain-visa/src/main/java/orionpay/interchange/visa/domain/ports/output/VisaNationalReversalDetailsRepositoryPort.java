package orionpay.interchange.visa.domain.ports.output;

import orionpay.interchange.visa.domain.model.VisaNationalReversalDetails;
import java.util.Optional;

public interface VisaNationalReversalDetailsRepositoryPort {
    VisaNationalReversalDetails save(VisaNationalReversalDetails reversalDetails);
    Optional<VisaNationalReversalDetails> findById(Long id);
}
