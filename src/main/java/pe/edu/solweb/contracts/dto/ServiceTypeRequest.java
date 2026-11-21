package pe.edu.solweb.contracts.dto;

import jakarta.validation.constraints.NotBlank;

public record ServiceTypeRequest(@NotBlank String name) {
}
