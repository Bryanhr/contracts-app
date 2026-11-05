package pe.edu.solweb.contracts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.solweb.contracts.dto.TemplateRequest;
import pe.edu.solweb.contracts.dto.TemplateResponse;
import pe.edu.solweb.contracts.entity.Plan;
import pe.edu.solweb.contracts.entity.Template;
import pe.edu.solweb.contracts.mappers.TemplateMapper;
import pe.edu.solweb.contracts.repository.TemplateRepository;
import pe.edu.solweb.contracts.service.PlanService;
import pe.edu.solweb.contracts.service.TemplateService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

    private final TemplateRepository templates;
    private final PlanService planService;
    private final TemplateMapper templateMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TemplateResponse> list(Long planId) {
        List<Template> found = planId == null
                ? templates.findAllByOrderByIdAsc()
                : templates.findByPlanIdOrderByIdAsc(planId);
        return found.stream().map(TemplateResponse::from).toList();
    }

    @Override
    @Transactional
    public TemplateResponse create(TemplateRequest request) {
        if (templates.existsByName(request.name())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A template named '" + request.name() + "' already exists");
        }
        Plan plan = planService.find(request.planId());
        return TemplateResponse.from(templates.save(templateMapper.toEntity(request, plan)));
    }

    @Override
    @Transactional
    public TemplateResponse update(Long id, TemplateRequest request) {
        if (templates.existsByNameAndIdNot(request.name(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A template named '" + request.name() + "' already exists");
        }
        Template template = find(id);
        Plan plan = planService.find(request.planId());
        templateMapper.update(template, request, plan);
        return TemplateResponse.from(templates.save(template));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        templates.delete(find(id));
    }

    private Template find(Long id) {
        return templates.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Template " + id + " not found"));
    }
}
