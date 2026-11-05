package pe.edu.solweb.contracts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.solweb.contracts.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
