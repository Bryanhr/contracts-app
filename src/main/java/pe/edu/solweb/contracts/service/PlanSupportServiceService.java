package pe.edu.solweb.contracts.service;

import pe.edu.solweb.contracts.dto.PlanServiceAddRequest;
import pe.edu.solweb.contracts.dto.PlanServiceResponse;
import pe.edu.solweb.contracts.dto.PlanServiceUpdateRequest;

import java.util.List;

public interface PlanSupportServiceService {
    List<PlanServiceResponse> list(Long planId);

    PlanServiceResponse add(Long planId, PlanServiceAddRequest request);

    PlanServiceResponse update(Long planId, Long supportServiceId, PlanServiceUpdateRequest request);

    void remove(Long planId, Long supportServiceId);
}
