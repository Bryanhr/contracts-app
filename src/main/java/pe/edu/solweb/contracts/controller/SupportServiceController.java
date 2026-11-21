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
import pe.edu.solweb.contracts.dto.SupportServiceRequest;
import pe.edu.solweb.contracts.dto.SupportServiceResponse;
import pe.edu.solweb.contracts.service.SupportServiceService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/support-services")
@RequiredArgsConstructor
public class SupportServiceController {

    private final SupportServiceService service;

    @GetMapping
    public List<SupportServiceResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public SupportServiceResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SupportServiceResponse create(@Valid @RequestBody SupportServiceRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public SupportServiceResponse update(@PathVariable Long id, @Valid @RequestBody SupportServiceRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
