package pe.edu.solweb.contracts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.solweb.contracts.dto.SupportServiceRequest;
import pe.edu.solweb.contracts.dto.SupportServiceResponse;
import pe.edu.solweb.contracts.entity.ServiceType;
import pe.edu.solweb.contracts.entity.SupportService;
import pe.edu.solweb.contracts.repository.PlanSupportServiceRepository;
import pe.edu.solweb.contracts.repository.ServiceTypeRepository;
import pe.edu.solweb.contracts.repository.SupportServiceRepository;
import pe.edu.solweb.contracts.service.SupportServiceService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupportServiceServiceImpl implements SupportServiceService {

    private final SupportServiceRepository repository;
    private final ServiceTypeRepository typeRepository;
    private final PlanSupportServiceRepository planServiceRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SupportServiceResponse> list() {
        return repository.findAll().stream().map(SupportServiceResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SupportServiceResponse get(Long id) {
        return SupportServiceResponse.from(find(id));
    }

    @Override
    @Transactional
    public SupportServiceResponse create(SupportServiceRequest request) {
        if (repository.existsByNameIgnoreCase(request.name().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A service with that name already exists");
        }
        SupportService service = new SupportService();
        apply(service, request);
        return SupportServiceResponse.from(repository.save(service));
    }

    @Override
    @Transactional
    public SupportServiceResponse update(Long id, SupportServiceRequest request) {
        SupportService service = find(id);
        if (repository.existsByNameIgnoreCaseAndIdNot(request.name().trim(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A service with that name already exists");
        }
        apply(service, request);
        return SupportServiceResponse.from(repository.save(service));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SupportService service = find(id);
        if (planServiceRepository.existsBySupportServiceId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Service " + id + " is associated with a plan");
        }
        repository.delete(service);
    }

    @Override
    @Transactional(readOnly = true)
    public SupportService find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service " + id + " not found"));
    }

    private void apply(SupportService service, SupportServiceRequest request) {
        ServiceType type = typeRepository.findById(request.serviceTypeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Service type " + request.serviceTypeId() + " not found"));
        service.setName(request.name().trim());
        service.setServiceType(type);
        service.setUnit(request.unit() == null ? null : request.unit().trim());
    }
}
