package orionpay.interchange.visa.infrastructure.adapters.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import orionpay.interchange.visa.infrastructure.adapters.output.persistence.entity.VisaFileControlEntity;

@Repository
public interface VisaFileControlJpaRepository extends JpaRepository<VisaFileControlEntity, Long> {
}
