package pe.edu.solweb.contracts.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private static final long EXPIRATION_MINUTES = 120;

    private final SecretKey key = Keys.hmacShaKeyFor(SimulatedLogin.SECRET.getBytes(StandardCharsets.UTF_8));

    public String issue(String subject) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(EXPIRATION_MINUTES * 60)))
                .signWith(key)
                .compact();
    }

    public String subject(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
