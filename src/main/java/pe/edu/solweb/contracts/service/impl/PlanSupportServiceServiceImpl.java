package pe.edu.solweb.contracts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.solweb.contracts.dto.PlanServiceAddRequest;
import pe.edu.solweb.contracts.dto.PlanServiceResponse;
import pe.edu.solweb.contracts.dto.PlanServiceUpdateRequest;
import pe.edu.solweb.contracts.entity.Plan;
import pe.edu.solweb.contracts.entity.PlanSupportService;
import pe.edu.solweb.contracts.entity.SupportService;
import pe.edu.solweb.contracts.repository.PlanSupportServiceRepository;
import pe.edu.solweb.contracts.service.PlanService;
import pe.edu.solweb.contracts.service.PlanSupportServiceService;
import pe.edu.solweb.contracts.service.SupportServiceService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanSupportServiceServiceImpl implements PlanSupportServiceService {

    private final PlanSupportServiceRepository repository;
    private final PlanService planService;
    private final SupportServiceService supportServiceService;

    @Override
    @Transactional(readOnly = true)
    public List<PlanServiceResponse> list(Long planId) {
        planService.find(planId);
        return repository.findByPlanId(planId).stream().map(PlanServiceResponse::from).toList();
    }

    @Override
    @Transactional
    public PlanServiceResponse add(Long planId, PlanServiceAddRequest request) {
        Plan plan = planService.find(planId);
        SupportService supportService = supportServiceService.find(request.supportServiceId());
        if (repository.existsByPlanIdAndSupportServiceId(planId, request.supportServiceId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The service is already associated with this plan");
        }
        PlanSupportService link = new PlanSupportService();
        link.setPlan(plan);
        link.setSupportService(supportService);
        link.setMonthlyLimit(request.monthlyLimit());
        link.setExtraCost(request.extraCost());
        return PlanServiceResponse.from(repository.save(link));
    }

    @Override
    @Transactional
    public PlanServiceResponse update(Long planId, Long supportServiceId, PlanServiceUpdateRequest request) {
        PlanSupportService link = findLink(planId, supportServiceId);
        link.setMonthlyLimit(request.monthlyLimit());
        link.setExtraCost(request.extraCost());
        return PlanServiceResponse.from(repository.save(link));
    }

    @Override
    @Transactional
    public void remove(Long planId, Long supportServiceId) {
        repository.delete(findLink(planId, supportServiceId));
    }

    private PlanSupportService findLink(Long planId, Long supportServiceId) {
        planService.find(planId);
        return repository.findByPlanIdAndSupportServiceId(planId, supportServiceId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "The plan does not have that service"));
    }
}
