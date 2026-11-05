package pe.edu.solweb.contracts.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.solweb.contracts.dto.LoginRequest;
import pe.edu.solweb.contracts.dto.LoginResponse;
import pe.edu.solweb.contracts.security.JwtService;
import pe.edu.solweb.contracts.security.SimulatedLogin;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService tokens;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        String email = SimulatedLogin.authenticate(request.email(), request.password())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));
        return new LoginResponse(tokens.issue(email));
    }
}
