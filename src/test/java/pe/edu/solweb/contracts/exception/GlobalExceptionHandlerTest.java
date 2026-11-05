package pe.edu.solweb.contracts.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new ProbeController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void notFoundUsesTheSameProblemBody() throws Exception {
        mvc.perform(get("/probe/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("Contrato no encontrado"))
                .andExpect(jsonPath("$.instance").value("/probe/missing"));
    }

    @Test
    void conflictAndUnsupportedMediaUseTheSameProblemBody() throws Exception {
        mvc.perform(get("/probe/conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").value("Conflict"))
                .andExpect(jsonPath("$.detail").value("Customer 1 has contracts"));

        mvc.perform(get("/probe/media"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.status").value(415))
                .andExpect(jsonPath("$.title").value("Unsupported Media Type"))
                .andExpect(jsonPath("$.detail").value("Solo se aceptan archivos PDF"));

        mvc.perform(get("/probe/duplicate"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("The data conflicts with an existing record"));
    }

    @Test
    void anUnexpectedErrorDoesNotLeakTheCause() throws Exception {
        mvc.perform(get("/probe/boom"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.title").value("Internal Server Error"))
                .andExpect(jsonPath("$.detail").value("An unexpected error occurred"))
                .andExpect(jsonPath("$.instance").value("/probe/boom"));
    }

    @RestController
    static class ProbeController {

        @GetMapping("/probe/missing")
        void missing() {
            throw new ResourceNotFoundException("Contrato no encontrado");
        }

        @GetMapping("/probe/conflict")
        void conflict() {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Customer 1 has contracts");
        }

        @GetMapping("/probe/media")
        void media() {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Solo se aceptan archivos PDF");
        }

        @GetMapping("/probe/duplicate")
        void duplicate() {
            throw new DataIntegrityViolationException("duplicate key");
        }

        @GetMapping("/probe/boom")
        void boom() {
            throw new IllegalStateException("secret detail from the database");
        }
    }
}
