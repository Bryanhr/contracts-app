package pe.edu.solweb.contracts.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.solweb.contracts.entity.Template;

import java.util.List;

public interface TemplateRepository extends JpaRepository<Template, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    boolean existsByPlanId(Long planId);

    @EntityGraph(attributePaths = "plan")
    List<Template> findAllByOrderByIdAsc();

    @EntityGraph(attributePaths = "plan")
    List<Template> findByPlanIdOrderByIdAsc(Long planId);

    List<Template> findByPlanId(Long planId);
}
