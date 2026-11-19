package pe.edu.solweb.contracts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.solweb.contracts.dto.ServiceTypeRequest;
import pe.edu.solweb.contracts.dto.ServiceTypeResponse;
import pe.edu.solweb.contracts.entity.ServiceType;
import pe.edu.solweb.contracts.repository.ServiceTypeRepository;
import pe.edu.solweb.contracts.repository.SupportServiceRepository;
import pe.edu.solweb.contracts.service.ServiceTypeService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceTypeServiceImpl implements ServiceTypeService {

    private final ServiceTypeRepository repository;
    private final SupportServiceRepository supportRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ServiceTypeResponse> list() {
        return repository.findAll().stream().map(ServiceTypeResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceTypeResponse get(Long id) {
        return ServiceTypeResponse.from(find(id));
    }

    @Override
    @Transactional
    public ServiceTypeResponse create(ServiceTypeRequest request) {
        String name = request.name().trim();
        if (repository.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A service type with that name already exists");
        }
        ServiceType type = new ServiceType();
        type.setName(name);
        return ServiceTypeResponse.from(repository.save(type));
    }

    @Override
    @Transactional
    public ServiceTypeResponse update(Long id, ServiceTypeRequest request) {
        ServiceType type = find(id);
        String name = request.name().trim();
        if (!type.getName().equalsIgnoreCase(name) && repository.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A service type with that name already exists");
        }
        type.setName(name);
        return ServiceTypeResponse.from(repository.save(type));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ServiceType type = find(id);
        if (supportRepository.existsByServiceTypeId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Service type " + id + " has registered services");
        }
        repository.delete(type);
    }

    private ServiceType find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service type " + id + " not found"));
    }
}
