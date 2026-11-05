package pe.edu.solweb.contracts.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.solweb.contracts.dto.ContractResponse;
import pe.edu.solweb.contracts.dto.CustomerResponse;
import pe.edu.solweb.contracts.entity.ContractStatus;
import pe.edu.solweb.contracts.entity.Validity;
import pe.edu.solweb.contracts.repository.ContractRepository;
import pe.edu.solweb.contracts.repository.PlanSupportServiceRepository;
import pe.edu.solweb.contracts.repository.TemplateRepository;
import pe.edu.solweb.contracts.service.ContractService;
import pe.edu.solweb.contracts.service.CustomerService;
import pe.edu.solweb.contracts.service.PdfService;
import pe.edu.solweb.contracts.service.StorageService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({CustomerController.class, ContractController.class})
class ResourceControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CustomerService customers;

    @MockitoBean
    private ContractService contracts;

    @MockitoBean
    private PdfService pdfService;

    @MockitoBean
    private StorageService storageService;

    @MockitoBean
    private ContractRepository contractRepository;

    @MockitoBean
    private TemplateRepository templateRepository;

    @MockitoBean
    private PlanSupportServiceRepository planSupportServices;

    @Test
    void customerLookupUpdateAndDeleteUseTheIdPath() throws Exception {
        when(customers.list(nullable(String.class))).thenReturn(List.of());
        when(customers.get(5L)).thenReturn(customer());
        when(customers.update(eq(5L), any())).thenReturn(customer());

        mvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk());
        mvc.perform(get("/api/v1/customers").param("search", "Andino"))
                .andExpect(status().isOk());
        verify(customers).list("Andino");
        mvc.perform(get("/api/v1/customers/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
        mvc.perform(put("/api/v1/customers/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"businessName":"ACME","ruc":"20123456789"}
                                """))
                .andExpect(status().isOk());
        mvc.perform(delete("/api/v1/customers/5"))
                .andExpect(status().isNoContent());

        verify(customers).get(5L);
        verify(customers).delete(5L);
    }

    @Test
    void contractIdPathDoesNotFallThroughToTheSearch() throws Exception {
        when(contracts.get(3L)).thenReturn(contract());
        when(contracts.search(ContractStatus.GENERATED, null, null)).thenReturn(List.of());
        when(contracts.search(null, null, Validity.ACTIVE)).thenReturn(List.of());

        mvc.perform(get("/api/v1/contracts/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3));
        mvc.perform(get("/api/v1/contracts").param("status", "GENERATED"))
                .andExpect(status().isOk());
        mvc.perform(get("/api/v1/contracts").param("validity", "ACTIVE"))
                .andExpect(status().isOk());

        verify(contracts).get(3L);
        verify(contracts, never()).search(any(), eq(3L), any());
        verify(contracts).search(ContractStatus.GENERATED, null, null);
        verify(contracts).search(null, null, Validity.ACTIVE);
    }

    private static CustomerResponse customer() {
        return new CustomerResponse(5L, "ACME", "20123456789", null, null, null, null);
    }

    private static ContractResponse contract() {
        return new ContractResponse(3L, 5L, "ACME", 1L, "Plan",
                LocalDate.of(2026, 11, 4), LocalDate.of(2026, 11, 1), LocalDate.of(2027, 11, 1),
                new BigDecimal("350.00"), ContractStatus.GENERATED, null, null);
    }
}
