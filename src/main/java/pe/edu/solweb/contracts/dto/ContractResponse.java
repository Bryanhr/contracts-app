package pe.edu.solweb.contracts.dto;

import pe.edu.solweb.contracts.entity.Contract;
import pe.edu.solweb.contracts.entity.ContractStatus;
import pe.edu.solweb.contracts.entity.Validity;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContractResponse(
        Long id,
        Long customerId, String customerName,
        Long planId, String planName,
        LocalDate issueDate, LocalDate startDate, LocalDate endDate,
        BigDecimal amount, ContractStatus status, LocalDate signedDate,
        Validity validity) {

    public static ContractResponse from(Contract c, LocalDate today) {
        return new ContractResponse(c.getId(),
                c.getCustomer().getId(), c.getCustomer().getBusinessName(),
                c.getPlan().getId(), c.getPlan().getName(),
                c.getIssueDate(), c.getStartDate(), c.getEndDate(),
                c.getAmount(), c.getStatus(), c.getSignedDate(),
                c.validityOn(today));
    }
}
