package pe.edu.solweb.contracts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.solweb.contracts.dto.CustomerRequest;
import pe.edu.solweb.contracts.dto.CustomerResponse;
import pe.edu.solweb.contracts.entity.Customer;
import pe.edu.solweb.contracts.mappers.CustomerMapper;
import pe.edu.solweb.contracts.repository.ContractRepository;
import pe.edu.solweb.contracts.repository.CustomerRepository;
import pe.edu.solweb.contracts.service.CustomerService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customers;
    private final ContractRepository contracts;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> list(String search) {
        String name = search == null ? "" : search.trim();
        List<Customer> found = name.isEmpty()
                ? customers.findAll()
                : customers.findByBusinessNameContainingIgnoreCase(name);
        return found.stream().map(CustomerResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse get(Long id) {
        return CustomerResponse.from(find(id));
    }

    @Override
    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        if (customers.existsByRuc(request.ruc())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A customer with RUC " + request.ruc() + " already exists");
        }
        return CustomerResponse.from(customers.save(customerMapper.toEntity(request)));
    }

    @Override
    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {
        if (customers.existsByRucAndIdNot(request.ruc(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A customer with RUC " + request.ruc() + " already exists");
        }
        Customer customer = find(id);
        customerMapper.update(customer, request);
        return CustomerResponse.from(customers.save(customer));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Customer customer = find(id);
        if (contracts.existsByCustomer_Id(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Customer " + id + " has contracts");
        }
        customers.delete(customer);
    }

    @Override
    public Customer find(Long id) {
        return customers.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer " + id + " not found"));
    }
}
