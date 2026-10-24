package pe.test.exchange.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.test.exchange.infrastructure.persistence.entity.ConversionEntity;

public interface SpringDataConversionRepository extends JpaRepository<ConversionEntity, Long> {
}
