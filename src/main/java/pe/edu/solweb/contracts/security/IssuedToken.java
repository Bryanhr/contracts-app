package pe.edu.solweb.contracts.security;

import java.time.Instant;

public record IssuedToken(String token, Instant expiresAt) {
}
