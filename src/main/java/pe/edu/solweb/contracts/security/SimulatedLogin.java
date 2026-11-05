package pe.edu.solweb.contracts.security;

import java.util.List;
import java.util.Optional;

public final class SimulatedLogin {

    public record Account(String email, String password) {
    }

    public static final List<Account> USERS = List.of(
            new Account("admin@empresa.pe", "admin123"),
            new Account("gerente@empresa.pe", "gerente123"),
            new Account("ventas1@empresa.pe", "ventas123"));

    /** Mínimo 32 bytes. Una clave más corta no arranca. */
    public static final String SECRET = "servit-demo-jwt-secret-key-32bytes";

    private SimulatedLogin() {
    }

    public static Optional<String> authenticate(String email, String password) {
        return USERS.stream()
                .filter(user -> user.email().equals(email) && user.password().equals(password))
                .map(Account::email)
                .findFirst();
    }
}
