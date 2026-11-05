package pe.edu.solweb.contracts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.solweb.contracts.entity.SupportService;

public interface SupportServiceRepository extends JpaRepository<SupportService, Long> {
}
