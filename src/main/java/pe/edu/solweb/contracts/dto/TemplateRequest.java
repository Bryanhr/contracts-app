package pe.edu.solweb.contracts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TemplateRequest(@NotNull Long planId, @NotBlank String name, @NotBlank String content) {
}
