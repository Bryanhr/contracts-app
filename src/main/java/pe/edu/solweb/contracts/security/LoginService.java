package pe.edu.solweb.contracts.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.solweb.contracts.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository users;
    private final PasswordEncoder passwords;

    public String authenticate(String email, String password) {
        return users.findByEmailIgnoreCase(email.trim())
                .filter(user -> passwords.matches(password, user.getPasswordHash()))
                .map(user -> user.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));
    }
}
