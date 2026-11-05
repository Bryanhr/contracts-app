package pe.edu.solweb.contracts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.solweb.contracts.entity.Contract;
import pe.edu.solweb.contracts.entity.ContractStatus;

import java.util.List;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    @Query("""
            select c from Contract c
            join fetch c.customer
            join fetch c.plan
            where (:status is null or c.status = :status)
              and (:customerId is null or c.customer.id = :customerId)
            order by c.id
            """)
    List<Contract> search(ContractStatus status, Long customerId);

    @Query("select c from Contract c join fetch c.customer join fetch c.plan where c.id = :id")
    Optional<Contract> findWithDetailsById(Long id);

    boolean existsByCustomer_Id(Long customerId);

    boolean existsByPlan_Id(Long planId);
}
