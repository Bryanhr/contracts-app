package pe.edu.solweb.contracts.mappers;

import org.springframework.stereotype.Component;
import pe.edu.solweb.contracts.dto.UserRequest;
import pe.edu.solweb.contracts.entity.User;

@Component
public class UserMapper {

    public User toEntity(UserRequest request) {
        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(request.email().trim());
        user.setRole(request.role().trim());
        return user;
    }
}
