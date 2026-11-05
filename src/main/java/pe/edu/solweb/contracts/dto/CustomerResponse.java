package pe.edu.solweb.contracts.dto;

import pe.edu.solweb.contracts.entity.Customer;

public record CustomerResponse(
        Long id, String businessName, String ruc, String representative,
        String email, String phone, String address) {

    public static CustomerResponse from(Customer c) {
        return new CustomerResponse(c.getId(), c.getBusinessName(), c.getRuc(), c.getRepresentative(),
                c.getEmail(), c.getPhone(), c.getAddress());
    }
}
