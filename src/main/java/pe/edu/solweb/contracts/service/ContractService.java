package pe.edu.solweb.contracts.service;

import pe.edu.solweb.contracts.dto.ContractRequest;
import pe.edu.solweb.contracts.dto.ContractResponse;
import pe.edu.solweb.contracts.entity.ContractStatus;
import pe.edu.solweb.contracts.entity.Validity;

import java.util.List;

public interface ContractService {

    List<ContractResponse> search(ContractStatus status, Long customerId, Validity validity);

    ContractResponse get(Long id);

    ContractResponse create(ContractRequest request);

    ContractResponse update(Long id, ContractRequest request);

    void delete(Long id);
}
