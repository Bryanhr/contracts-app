package pe.edu.solweb.contracts.service;

import pe.edu.solweb.contracts.dto.SupportServiceRequest;
import pe.edu.solweb.contracts.dto.SupportServiceResponse;
import pe.edu.solweb.contracts.entity.SupportService;

import java.util.List;

public interface SupportServiceService {
    List<SupportServiceResponse> list();

    SupportServiceResponse get(Long id);

    SupportServiceResponse create(SupportServiceRequest request);

    SupportServiceResponse update(Long id, SupportServiceRequest request);

    void delete(Long id);

    SupportService find(Long id);
}
