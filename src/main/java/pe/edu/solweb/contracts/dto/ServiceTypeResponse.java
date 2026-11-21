package pe.edu.solweb.contracts.dto;

import pe.edu.solweb.contracts.entity.ServiceType;

public record ServiceTypeResponse(Long id, String name) {
    public static ServiceTypeResponse from(ServiceType type) {
        return new ServiceTypeResponse(type.getId(), type.getName());
    }
}
