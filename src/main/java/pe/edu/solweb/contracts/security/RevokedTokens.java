package pe.edu.solweb.contracts.security;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

public class RevokedTokens {

    private final ConcurrentHashMap<String, Instant> until = new ConcurrentHashMap<>();

    public void revoke(String id, Instant expiresAt) {
        until.put(id, expiresAt);
        dropExpired();
    }

    public boolean isRevoked(String id) {
        Instant expiresAt = until.get(id);
        if (expiresAt == null) {
            return false;
        }
        if (!expiresAt.isAfter(Instant.now())) {
            until.remove(id);
            return false;
        }
        return true;
    }

    private void dropExpired() {
        Instant now = Instant.now();
        until.entrySet().removeIf(entry -> !entry.getValue().isAfter(now));
    }
}
