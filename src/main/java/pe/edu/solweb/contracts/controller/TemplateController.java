package pe.edu.solweb.contracts.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.solweb.contracts.dto.TemplateRequest;
import pe.edu.solweb.contracts.dto.TemplateResponse;
import pe.edu.solweb.contracts.service.TemplateService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService service;

    @GetMapping
    public List<TemplateResponse> list(@RequestParam(required = false) Long planId) {
        return service.list(planId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TemplateResponse create(@Valid @RequestBody TemplateRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public TemplateResponse update(@PathVariable Long id, @Valid @RequestBody TemplateRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
