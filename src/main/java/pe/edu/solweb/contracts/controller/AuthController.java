package pe.edu.solweb.contracts.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.solweb.contracts.dto.LoginRequest;
import pe.edu.solweb.contracts.dto.LoginResponse;
import pe.edu.solweb.contracts.security.IssuedToken;
import pe.edu.solweb.contracts.security.JwtService;
import pe.edu.solweb.contracts.security.LoginService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService tokens;
    private final LoginService login;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        IssuedToken issued = tokens.issue(login.authenticate(request.email(), request.password()));
        return new LoginResponse(issued.token(), issued.expiresAt());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        tokens.revoke(authorization.substring(7).trim());
    }
}
