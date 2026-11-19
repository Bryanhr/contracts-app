package pe.edu.solweb.contracts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SupportServiceRequest(
        @NotBlank String name,
        @NotNull Long serviceTypeId,
        String unit) {
}
