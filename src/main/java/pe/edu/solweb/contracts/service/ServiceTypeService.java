package pe.edu.solweb.contracts.service;

import pe.edu.solweb.contracts.dto.ServiceTypeRequest;
import pe.edu.solweb.contracts.dto.ServiceTypeResponse;

import java.util.List;

public interface ServiceTypeService {
    List<ServiceTypeResponse> list();

    ServiceTypeResponse get(Long id);

    ServiceTypeResponse create(ServiceTypeRequest request);

    ServiceTypeResponse update(Long id, ServiceTypeRequest request);

    void delete(Long id);
}
