package pe.edu.solweb.contracts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.solweb.contracts.dto.ContractRequest;
import pe.edu.solweb.contracts.dto.ContractResponse;
import pe.edu.solweb.contracts.entity.Contract;
import pe.edu.solweb.contracts.entity.ContractStatus;
import pe.edu.solweb.contracts.entity.Customer;
import pe.edu.solweb.contracts.entity.Plan;
import pe.edu.solweb.contracts.entity.Validity;
import pe.edu.solweb.contracts.mappers.ContractMapper;
import pe.edu.solweb.contracts.repository.ContractRepository;
import pe.edu.solweb.contracts.service.ContractService;
import pe.edu.solweb.contracts.service.CustomerService;
import pe.edu.solweb.contracts.service.PlanService;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contracts;
    private final CustomerService customerService;
    private final PlanService planService;
    private final ContractMapper contractMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ContractResponse> search(ContractStatus status, Long customerId, Validity validity) {
        LocalDate today = LocalDate.now();
        return contracts.search(status, customerId).stream()
                .filter(contract -> validity == null || validity == contract.validityOn(today))
                .map(contract -> ContractResponse.from(contract, today))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ContractResponse get(Long id) {
        Contract contract = contracts.findWithDetailsById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contract " + id + " not found"));
        return ContractResponse.from(contract, LocalDate.now());
    }

    @Override
    @Transactional
    public ContractResponse create(ContractRequest request) {
        Customer customer = customerService.find(request.customerId());
        Plan plan = planService.find(request.planId());
        return ContractResponse.from(
                contracts.save(contractMapper.toEntity(request, customer, plan)), LocalDate.now());
    }

    @Override
    @Transactional
    public ContractResponse update(Long id, ContractRequest request) {
        Contract contract = contracts.findWithDetailsById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contract " + id + " not found"));
        Customer customer = customerService.find(request.customerId());
        Plan plan = planService.find(request.planId());
        contractMapper.update(contract, request, customer, plan);
        return ContractResponse.from(contracts.save(contract), LocalDate.now());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Contract contract = contracts.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contract " + id + " not found"));
        contracts.delete(contract);
    }
}
