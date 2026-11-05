package pe.edu.solweb.contracts.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ContractRequest(@NotNull Long customerId, @NotNull Long planId, @NotNull LocalDate startDate) {
}
