package pe.edu.solweb.contracts.service;

import pe.edu.solweb.contracts.dto.UserRequest;
import pe.edu.solweb.contracts.dto.UserResponse;

public interface UserService {

    UserResponse create(UserRequest request);
}
