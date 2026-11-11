package pe.edu.solweb.contracts.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.solweb.contracts.config.SecurityConfig;
import pe.edu.solweb.contracts.controller.AuthController;
import pe.edu.solweb.contracts.controller.CustomerController;
import pe.edu.solweb.contracts.controller.PlanController;
import pe.edu.solweb.contracts.controller.UserController;
import pe.edu.solweb.contracts.dto.UserResponse;
import pe.edu.solweb.contracts.entity.User;
import pe.edu.solweb.contracts.repository.UserRepository;
import pe.edu.solweb.contracts.service.CustomerService;
import pe.edu.solweb.contracts.service.PlanService;
import pe.edu.solweb.contracts.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AuthController.class, CustomerController.class, PlanController.class, UserController.class})
@Import({SecurityConfig.class, JwtService.class, RestAuthenticationEntryPoint.class, LoginService.class})
@ImportAutoConfiguration({ServletWebSecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
class SecurityAccessTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JwtService tokens;

    @MockitoBean
    private CustomerService customers;

    @MockitoBean
    private PlanService plans;

    @MockitoBean
    private UserRepository users;

    @MockitoBean
    private UserService userAccounts;

    @Autowired
    private PasswordEncoder passwords;

    @BeforeEach
    void usersInTheTable() {
        when(users.findByEmailIgnoreCase(anyString())).thenReturn(Optional.empty());
        account("admin@empresa.pe", "admin123");
        account("gerente@empresa.pe", "gerente123");
        account("ventas1@empresa.pe", "ventas123");
    }

    @Test
    void angularLocalOriginCanCallTheApi() throws Exception {
        mvc.perform(options("/api/v1/customers")
                        .header("Origin", "http://localhost:4200")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "Authorization"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    @Test
    void aProtectedRouteRejectsAMissingToken() throws Exception {
        mvc.perform(get("/api/v1/customers"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.title").value("Unauthorized"))
                .andExpect(jsonPath("$.detail").value("Se requiere un token"));
    }

    @Test
    void aProtectedRouteAcceptsTheIssuedToken() throws Exception {
        when(customers.list(nullable(String.class))).thenReturn(List.of());

        mvc.perform(get("/api/v1/customers").header("Authorization", "Bearer " + tokens.issue("admin@empresa.pe").token()))
                .andExpect(status().isOk());
    }

    @Test
    void creatingAUserDoesNotRequireAToken() throws Exception {
        when(userAccounts.create(any())).thenReturn(new UserResponse(1L, "Ana", "ana@empresa.pe", "SALES"));

        mvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Ana","email":"ana@empresa.pe","password":"clave1234","role":"SALES"}
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void aCatalogStaysPublic() throws Exception {
        when(plans.list()).thenReturn(List.of());

        mvc.perform(get("/api/v1/plans"))
                .andExpect(status().isOk());
    }

    @Test
    void logoutRejectsTheSameTokenAfterwards() throws Exception {
        when(customers.list(nullable(String.class))).thenReturn(List.of());
        String closed = tokens.issue("admin@empresa.pe").token();
        String stillOpen = tokens.issue("gerente@empresa.pe").token();

        mvc.perform(post("/api/v1/auth/logout").header("Authorization", "Bearer " + closed))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/v1/customers").header("Authorization", "Bearer " + closed))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/customers").header("Authorization", "Bearer " + stillOpen))
                .andExpect(status().isOk());
    }

    @Test
    void eachStoredUserReceivesAToken() throws Exception {
        for (String[] user : new String[][]{
                {"admin@empresa.pe", "admin123"},
                {"gerente@empresa.pe", "gerente123"},
                {"ventas1@empresa.pe", "ventas123"}}) {
            mvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"email":"%s","password":"%s"}
                                    """.formatted(user[0], user[1])))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isNotEmpty())
                    .andExpect(jsonPath("$.expiresAt").isNotEmpty());
        }
    }

    @Test
    void loginRejectsAWrongPassword() throws Exception {
        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"admin@empresa.pe","password":"no-es"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    private void account(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwords.encode(password));
        when(users.findByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
    }
}
