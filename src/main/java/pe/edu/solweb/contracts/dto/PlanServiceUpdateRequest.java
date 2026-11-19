package pe.edu.solweb.contracts.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record PlanServiceUpdateRequest(
        @NotNull @PositiveOrZero Integer monthlyLimit,
        @NotNull @PositiveOrZero @Digits(integer = 8, fraction = 2) BigDecimal extraCost) {
}
