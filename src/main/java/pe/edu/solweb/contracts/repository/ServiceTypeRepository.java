package pe.edu.solweb.contracts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.solweb.contracts.entity.ServiceType;

public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {
}
