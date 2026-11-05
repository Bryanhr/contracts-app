package pe.edu.solweb.contracts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.solweb.contracts.entity.Plan;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
