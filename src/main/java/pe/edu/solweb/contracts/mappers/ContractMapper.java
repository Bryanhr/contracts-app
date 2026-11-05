package pe.edu.solweb.contracts.mappers;

import org.springframework.stereotype.Component;
import pe.edu.solweb.contracts.dto.ContractRequest;
import pe.edu.solweb.contracts.entity.Contract;
import pe.edu.solweb.contracts.entity.ContractStatus;
import pe.edu.solweb.contracts.entity.Customer;
import pe.edu.solweb.contracts.entity.Plan;

import java.time.LocalDate;

@Component
public class ContractMapper {

    public Contract toEntity(ContractRequest request, Customer customer, Plan plan) {
        Contract contract = new Contract();
        contract.setCustomer(customer);
        contract.setPlan(plan);
        contract.setIssueDate(LocalDate.now());
        contract.setStartDate(request.startDate());
        contract.setEndDate(request.startDate().plusMonths(plan.getDurationMonths()));
        contract.setAmount(plan.getMonthlyCost());
        contract.setStatus(ContractStatus.GENERATED);
        return contract;
    }

    public void update(Contract contract, ContractRequest request, Customer customer, Plan plan) {
        contract.setCustomer(customer);
        contract.setPlan(plan);
        contract.setStartDate(request.startDate());
        contract.setEndDate(request.startDate().plusMonths(plan.getDurationMonths()));
        contract.setAmount(plan.getMonthlyCost());
    }
}
