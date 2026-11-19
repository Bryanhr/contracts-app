package pe.edu.solweb.contracts.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.solweb.contracts.entity.PlanSupportService;

import java.util.List;
import java.util.Optional;

public interface PlanSupportServiceRepository extends JpaRepository<PlanSupportService, Long> {

    @EntityGraph(attributePaths = {"supportService", "supportService.serviceType"})
    List<PlanSupportService> findByPlanId(Long planId);

    Optional<PlanSupportService> findByPlanIdAndSupportServiceId(Long planId, Long supportServiceId);

    boolean existsByPlanIdAndSupportServiceId(Long planId, Long supportServiceId);

    boolean existsBySupportServiceId(Long supportServiceId);

    boolean existsByPlanId(Long planId);
}
