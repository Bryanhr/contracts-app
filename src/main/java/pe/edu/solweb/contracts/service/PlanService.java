package pe.edu.solweb.contracts.service;

import pe.edu.solweb.contracts.dto.PlanDetailResponse;
import pe.edu.solweb.contracts.dto.PlanRequest;
import pe.edu.solweb.contracts.dto.PlanResponse;
import pe.edu.solweb.contracts.entity.Plan;

import java.util.List;

public interface PlanService {

    List<PlanResponse> list();

    PlanDetailResponse get(Long id);

    PlanResponse create(PlanRequest request);

    PlanResponse update(Long id, PlanRequest request);

    void delete(Long id);

    Plan find(Long id);
}
