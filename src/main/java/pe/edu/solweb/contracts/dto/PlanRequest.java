package pe.edu.solweb.contracts.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record PlanRequest(
        @NotBlank String name,
        @NotNull @Positive Integer responseTimeHours,
        @NotNull @Positive @Digits(integer = 8, fraction = 2) BigDecimal monthlyCost,
        @NotNull @Min(1) Integer durationMonths) {
}
