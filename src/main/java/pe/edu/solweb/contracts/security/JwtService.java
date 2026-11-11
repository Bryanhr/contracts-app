package pe.edu.solweb.contracts.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private static final long EXPIRATION_MINUTES = 120;

    /** Mínimo 32 bytes. Una clave más corta no arranca. */
    private static final String SECRET = "servit-demo-jwt-secret-key-32bytes";

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    private final RevokedTokens revoked;

    public JwtService(RevokedTokens revoked) {
        this.revoked = revoked;
    }

    public IssuedToken issue(String subject) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(EXPIRATION_MINUTES * 60);
        String token = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(key)
                .compact();
        return new IssuedToken(token, expiresAt);
    }

    public void revoke(String token) {
        Claims claims = claims(token);
        revoked.revoke(claims.getId(), claims.getExpiration().toInstant());
    }

    public String subject(String token) {
        Claims claims = claims(token);
        if (revoked.isRevoked(claims.getId())) {
            throw new JwtException("revoked");
        }
        return claims.getSubject();
    }

    private Claims claims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
