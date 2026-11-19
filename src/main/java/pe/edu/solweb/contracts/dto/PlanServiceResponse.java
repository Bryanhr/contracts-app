package pe.edu.solweb.contracts.dto;

import pe.edu.solweb.contracts.entity.PlanSupportService;
import pe.edu.solweb.contracts.entity.SupportService;

import java.math.BigDecimal;

public record PlanServiceResponse(
        Long supportServiceId,
        String name,
        String serviceType,
        String unit,
        Integer monthlyLimit,
        BigDecimal extraCost) {

    public static PlanServiceResponse from(PlanSupportService link) {
        SupportService service = link.getSupportService();
        String type = service.getServiceType() == null ? null : service.getServiceType().getName();
        return new PlanServiceResponse(
                service.getId(),
                service.getName(),
                blankToNull(type),
                blankToNull(service.getUnit()),
                link.getMonthlyLimit(),
                link.getExtraCost());
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}