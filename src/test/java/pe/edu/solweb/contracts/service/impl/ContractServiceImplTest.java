package pe.edu.solweb.contracts.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.solweb.contracts.dto.ContractResponse;
import pe.edu.solweb.contracts.entity.Contract;
import pe.edu.solweb.contracts.entity.ContractStatus;
import pe.edu.solweb.contracts.entity.Customer;
import pe.edu.solweb.contracts.entity.Plan;
import pe.edu.solweb.contracts.entity.Validity;
import pe.edu.solweb.contracts.mappers.ContractMapper;
import pe.edu.solweb.contracts.repository.ContractRepository;
import pe.edu.solweb.contracts.service.CustomerService;
import pe.edu.solweb.contracts.service.PlanService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContractServiceImplTest {

    @Mock
    private ContractRepository contracts;
    @Mock
    private CustomerService customerService;
    @Mock
    private PlanService planService;
    @Mock
    private ContractMapper contractMapper;
    @InjectMocks
    private ContractServiceImpl service;

    @Test
    void searchByValidityUsesTheCalculatedRule() {
        LocalDate today = LocalDate.now();
        Contract active = contract(1L, ContractStatus.SIGNED, today.minusDays(5), today.plusDays(40));
        Contract expiring = contract(2L, ContractStatus.SIGNED, today.minusDays(5), today.plusDays(10));
        Contract unsigned = contract(3L, ContractStatus.GENERATED, today.minusDays(5), today.plusDays(40));
        when(contracts.search(null, null)).thenReturn(List.of(active, expiring, unsigned));

        List<Long> found = service.search(null, null, Validity.ACTIVE).stream()
                .map(ContractResponse::id)
                .toList();

        assertEquals(List.of(1L), found);
    }

    private static Contract contract(Long id, ContractStatus status, LocalDate start, LocalDate end) {
        Customer customer = new Customer();
        customer.setId(10L);
        customer.setBusinessName("ACME");
        Plan plan = new Plan();
        plan.setId(20L);
        plan.setName("Plan");
        Contract contract = new Contract();
        contract.setId(id);
        contract.setCustomer(customer);
        contract.setPlan(plan);
        contract.setStatus(status);
        contract.setIssueDate(start);
        contract.setStartDate(start);
        contract.setEndDate(end);
        contract.setAmount(new BigDecimal("100.00"));
        return contract;
    }
}
