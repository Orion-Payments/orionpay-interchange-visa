package orionpay.interchange.visa.domain.ports.output;

import orionpay.interchange.visa.domain.model.VisaDisputeManagement;
import java.util.Optional;

public interface VisaDisputeManagementRepositoryPort {
    VisaDisputeManagement save(VisaDisputeManagement dispute);
    Optional<VisaDisputeManagement> findById(Long id);
}
