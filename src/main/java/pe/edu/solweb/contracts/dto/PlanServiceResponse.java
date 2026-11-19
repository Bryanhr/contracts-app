package pe.edu.solweb.contracts.dto;

import pe.edu.solweb.contracts.entity.PlanSupportService;
import pe.edu.solweb.contracts.entity.SupportService;

import java.math.BigDecimal;

public record PlanServiceResponse(
        Long supportServiceId, String name, String serviceType, String unit,
        Integer monthlyLimit, BigDecimal extraCost) {
    public static PlanServiceResponse from(PlanSupportService link) {
        SupportService service = link.getSupportService();
        return new PlanServiceResponse(
                service.getId(),
                service.getName(),
                service.getServiceType().getName(),
                service.getUnit(),
                link.getMonthlyLimit(),
                link.getExtraCost());
    }
}
