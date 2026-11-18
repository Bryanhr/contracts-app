package pe.edu.solweb.contracts.dto;

import pe.edu.solweb.contracts.entity.Plan;
import pe.edu.solweb.contracts.entity.PlanSupportService;

import java.math.BigDecimal;
import java.util.List;

public record PlanDetailResponse(
        Long id,
        String name,
        Integer responseTimeHours,
        BigDecimal monthlyCost,
        Integer durationMonths,
        List<PlanServiceResponse> services) {

    public static PlanDetailResponse from(Plan plan, List<PlanSupportService> planServices) {
        List<PlanServiceResponse> services = planServices == null
                ? List.of()
                : planServices.stream().map(PlanServiceResponse::from).toList();
        return new PlanDetailResponse(
                plan.getId(),
                plan.getName(),
                plan.getResponseTimeHours(),
                plan.getMonthlyCost(),
                plan.getDurationMonths(),
                services);
    }
}
