package orionpay.interchange.visa.domain.ports.output;

import orionpay.interchange.visa.domain.model.VisaClearingPresentation;
import java.util.Optional;

public interface VisaClearingPresentationRepositoryPort {
    VisaClearingPresentation save(VisaClearingPresentation presentation);
    Optional<VisaClearingPresentation> findById(Long id);
}
