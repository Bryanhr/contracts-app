package pe.edu.solweb.contracts.mappers;

import org.springframework.stereotype.Component;
import pe.edu.solweb.contracts.dto.CustomerRequest;
import pe.edu.solweb.contracts.entity.Customer;

@Component
public class CustomerMapper {

    public Customer toEntity(CustomerRequest request) {
        Customer customer = new Customer();
        apply(customer, request);
        return customer;
    }

    public void update(Customer customer, CustomerRequest request) {
        apply(customer, request);
    }

    private void apply(Customer customer, CustomerRequest request) {
        customer.setBusinessName(request.businessName());
        customer.setRuc(request.ruc());
        customer.setRepresentative(request.representative());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());
        customer.setAddress(request.address());
    }
}
