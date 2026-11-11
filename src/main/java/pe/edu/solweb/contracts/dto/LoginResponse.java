package pe.edu.solweb.contracts.dto;

import java.time.Instant;

public record LoginResponse(String token, Instant expiresAt) {
}
