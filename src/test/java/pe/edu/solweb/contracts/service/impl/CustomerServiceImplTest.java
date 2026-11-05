package pe.edu.solweb.contracts.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.solweb.contracts.entity.Customer;
import pe.edu.solweb.contracts.mappers.CustomerMapper;
import pe.edu.solweb.contracts.repository.ContractRepository;
import pe.edu.solweb.contracts.repository.CustomerRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customers;
    @Mock
    private ContractRepository contracts;
    @Mock
    private CustomerMapper customerMapper;
    @InjectMocks
    private CustomerServiceImpl service;

    @Test
    void searchUsesTheBusinessNameIgnoringCase() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setBusinessName("Soporte Andino SAC");
        customer.setRuc("20123456789");
        when(customers.findByBusinessNameContainingIgnoreCase("andino")).thenReturn(List.of(customer));

        var found = service.list("  andino ");

        assertEquals(1, found.size());
        assertEquals("Soporte Andino SAC", found.getFirst().businessName());
        verify(customers).findByBusinessNameContainingIgnoreCase("andino");
    }
}
