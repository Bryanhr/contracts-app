package pe.edu.solweb.contracts.service;

import pe.edu.solweb.contracts.dto.CustomerRequest;
import pe.edu.solweb.contracts.dto.CustomerResponse;
import pe.edu.solweb.contracts.entity.Customer;

import java.util.List;

public interface CustomerService {

    List<CustomerResponse> list(String search);

    CustomerResponse get(Long id);

    CustomerResponse create(CustomerRequest request);

    CustomerResponse update(Long id, CustomerRequest request);

    void delete(Long id);

    Customer find(Long id);
}
