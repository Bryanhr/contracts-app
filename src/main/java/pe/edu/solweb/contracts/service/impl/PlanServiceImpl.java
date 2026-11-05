package pe.edu.solweb.contracts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.solweb.contracts.dto.PlanRequest;
import pe.edu.solweb.contracts.dto.PlanResponse;
import pe.edu.solweb.contracts.entity.Plan;
import pe.edu.solweb.contracts.mappers.PlanMapper;
import pe.edu.solweb.contracts.repository.ContractRepository;
import pe.edu.solweb.contracts.repository.PlanRepository;
import pe.edu.solweb.contracts.repository.TemplateRepository;
import pe.edu.solweb.contracts.service.PlanService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository plans;
    private final ContractRepository contracts;
    private final TemplateRepository templates;
    private final PlanMapper planMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PlanResponse> list() {
        return plans.findAll().stream().map(PlanResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PlanResponse get(Long id) {
        return PlanResponse.from(find(id));
    }

    @Override
    @Transactional
    public PlanResponse create(PlanRequest request) {
        if (plans.existsByName(request.name())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A plan named '" + request.name() + "' already exists");
        }
        return PlanResponse.from(plans.save(planMapper.toEntity(request)));
    }

    @Override
    @Transactional
    public PlanResponse update(Long id, PlanRequest request) {
        if (plans.existsByNameAndIdNot(request.name(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A plan named '" + request.name() + "' already exists");
        }
        Plan plan = find(id);
        planMapper.update(plan, request);
        return PlanResponse.from(plans.save(plan));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Plan plan = find(id);
        if (contracts.existsByPlan_Id(id) || templates.existsByPlanId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Plan " + id + " is used by a contract or a template");
        }
        plans.delete(plan);
    }

    @Override
    public Plan find(Long id) {
        return plans.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan " + id + " not found"));
    }
}
