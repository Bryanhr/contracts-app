package pe.edu.solweb.contracts.mappers;

import org.springframework.stereotype.Component;
import pe.edu.solweb.contracts.dto.PlanRequest;
import pe.edu.solweb.contracts.entity.Plan;

@Component
public class PlanMapper {

    public Plan toEntity(PlanRequest request) {
        Plan plan = new Plan();
        apply(plan, request);
        return plan;
    }

    public void update(Plan plan, PlanRequest request) {
        apply(plan, request);
    }

    private void apply(Plan plan, PlanRequest request) {
        plan.setName(request.name());
        plan.setResponseTimeHours(request.responseTimeHours());
        plan.setMonthlyCost(request.monthlyCost());
        plan.setDurationMonths(request.durationMonths());
    }
}
