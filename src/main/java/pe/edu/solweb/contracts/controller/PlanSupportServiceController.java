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
import pe.edu.solweb.contracts.dto.PlanServiceAddRequest;
import pe.edu.solweb.contracts.dto.PlanServiceResponse;
import pe.edu.solweb.contracts.dto.PlanServiceUpdateRequest;
import pe.edu.solweb.contracts.service.PlanSupportServiceService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/plans/{planId}/services")
@RequiredArgsConstructor
public class PlanSupportServiceController {

    private final PlanSupportServiceService service;

    @GetMapping
    public List<PlanServiceResponse> list(@PathVariable Long planId) {
        return service.list(planId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlanServiceResponse add(
            @PathVariable Long planId, @Valid @RequestBody PlanServiceAddRequest request) {
        return service.add(planId, request);
    }

    @PutMapping("/{serviceId}")
    public PlanServiceResponse update(
            @PathVariable Long planId,
            @PathVariable Long serviceId,
            @Valid @RequestBody PlanServiceUpdateRequest request) {
        return service.update(planId, serviceId, request);
    }

    @DeleteMapping("/{serviceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long planId, @PathVariable Long serviceId) {
        service.remove(planId, serviceId);
    }
}
