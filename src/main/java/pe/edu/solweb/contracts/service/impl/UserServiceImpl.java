package pe.edu.solweb.contracts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.solweb.contracts.dto.UserRequest;
import pe.edu.solweb.contracts.dto.UserResponse;
import pe.edu.solweb.contracts.entity.User;
import pe.edu.solweb.contracts.mappers.UserMapper;
import pe.edu.solweb.contracts.repository.UserRepository;
import pe.edu.solweb.contracts.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository users;
    private final PasswordEncoder passwords;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse create(UserRequest request) {
        String email = request.email().trim();
        if (users.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A user with email " + email + " already exists");
        }
        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwords.encode(request.password()));
        return UserResponse.from(users.save(user));
    }
}
