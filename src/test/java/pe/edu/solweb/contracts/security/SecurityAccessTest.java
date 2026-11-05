package pe.edu.solweb.contracts.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.solweb.contracts.config.SecurityConfig;
import pe.edu.solweb.contracts.controller.AuthController;
import pe.edu.solweb.contracts.controller.CustomerController;
import pe.edu.solweb.contracts.controller.PlanController;
import pe.edu.solweb.contracts.service.CustomerService;
import pe.edu.solweb.contracts.service.PlanService;

import java.util.List;

import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AuthController.class, CustomerController.class, PlanController.class})
@Import({SecurityConfig.class, JwtService.class, RestAuthenticationEntryPoint.class})
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

        mvc.perform(get("/api/v1/customers").header("Authorization", "Bearer " + tokens.issue(SimulatedLogin.USERS.getFirst().email())))
                .andExpect(status().isOk());
    }

    @Test
    void aCatalogStaysPublic() throws Exception {
        when(plans.list()).thenReturn(List.of());

        mvc.perform(get("/api/v1/plans"))
                .andExpect(status().isOk());
    }

    @Test
    void eachSimulatedUserReceivesAToken() throws Exception {
        for (SimulatedLogin.Account user : SimulatedLogin.USERS) {
            mvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"email":"%s","password":"%s"}
                                    """.formatted(user.email(), user.password())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isNotEmpty());
        }
    }
}
