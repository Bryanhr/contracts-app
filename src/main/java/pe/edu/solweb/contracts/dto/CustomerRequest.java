package pe.edu.solweb.contracts.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CustomerRequest(
        @NotBlank String businessName,
        @NotBlank @Pattern(regexp = "\\d{11}", message = "must have 11 digits") String ruc,
        String representative,
        @Email String email,
        String phone,
        String address) {
}
