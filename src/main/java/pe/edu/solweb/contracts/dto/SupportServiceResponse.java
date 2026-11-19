package pe.edu.solweb.contracts.dto;

import pe.edu.solweb.contracts.entity.SupportService;

public record SupportServiceResponse(
        Long id, String name, Long serviceTypeId, String serviceTypeName, String unit) {
    public static SupportServiceResponse from(SupportService service) {
        return new SupportServiceResponse(
                service.getId(),
                service.getName(),
                service.getServiceType().getId(),
                service.getServiceType().getName(),
                service.getUnit());
    }
}
