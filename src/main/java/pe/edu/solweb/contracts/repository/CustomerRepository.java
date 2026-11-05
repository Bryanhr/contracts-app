package pe.edu.solweb.contracts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.solweb.contracts.entity.Customer;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByBusinessNameContainingIgnoreCase(String businessName);

    boolean existsByRuc(String ruc);

    boolean existsByRucAndIdNot(String ruc, Long id);
}
