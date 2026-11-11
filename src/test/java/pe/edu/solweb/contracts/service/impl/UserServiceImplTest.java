package pe.edu.solweb.contracts.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.solweb.contracts.dto.UserRequest;
import pe.edu.solweb.contracts.dto.UserResponse;
import pe.edu.solweb.contracts.entity.User;
import pe.edu.solweb.contracts.mappers.UserMapper;
import pe.edu.solweb.contracts.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    private final UserRepository users = mock(UserRepository.class);
    private final PasswordEncoder passwords = mock(PasswordEncoder.class);
    private final UserServiceImpl service = new UserServiceImpl(users, passwords, new UserMapper());

    @Test
    void createStoresTheHashAndHidesThePassword() {
        when(users.existsByEmailIgnoreCase("nuevo@empresa.pe")).thenReturn(false);
        when(passwords.encode("clave1234")).thenReturn("$2a$10$hash");
        when(users.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(9L);
            return user;
        });

        UserResponse response = service.create(new UserRequest("Ana", "nuevo@empresa.pe", "clave1234", "SALES"));

        assertEquals(9L, response.id());
        assertEquals("Ana", response.name());
        assertEquals("nuevo@empresa.pe", response.email());
        assertEquals("SALES", response.role());
        verify(users).save(argThat(user -> "$2a$10$hash".equals(user.getPasswordHash())));
    }

    @Test
    void createRejectsARepeatedEmail() {
        when(users.existsByEmailIgnoreCase("admin@empresa.pe")).thenReturn(true);

        ResponseStatusException error = assertThrows(ResponseStatusException.class, () ->
                service.create(new UserRequest("Ana", "admin@empresa.pe", "clave1234", "SALES")));

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
    }
}
