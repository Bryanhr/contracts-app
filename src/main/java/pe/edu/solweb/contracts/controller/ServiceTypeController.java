package pe.edu.solweb.contracts.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.solweb.contracts.dto.ServiceTypeRequest;
import pe.edu.solweb.contracts.dto.ServiceTypeResponse;
import pe.edu.solweb.contracts.service.ServiceTypeService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/service-types")
@RequiredArgsConstructor
public class ServiceTypeController {

    private final ServiceTypeService service;

    @GetMapping
    public List<ServiceTypeResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public ServiceTypeResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceTypeResponse create(@Valid @RequestBody ServiceTypeRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public ServiceTypeResponse update(@PathVariable Long id, @Valid @RequestBody ServiceTypeRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
