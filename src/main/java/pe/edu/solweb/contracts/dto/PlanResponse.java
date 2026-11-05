package pe.edu.solweb.contracts.dto;

import pe.edu.solweb.contracts.entity.Plan;

import java.math.BigDecimal;

public record PlanResponse(Long id, String name, Integer responseTimeHours, BigDecimal monthlyCost, Integer durationMonths) {

    public static PlanResponse from(Plan p) {
        return new PlanResponse(p.getId(), p.getName(), p.getResponseTimeHours(), p.getMonthlyCost(), p.getDurationMonths());
    }
}
