package pe.edu.solweb.contracts.service;

import pe.edu.solweb.contracts.dto.TemplateRequest;
import pe.edu.solweb.contracts.dto.TemplateResponse;

import java.util.List;

public interface TemplateService {

    List<TemplateResponse> list(Long planId);

    TemplateResponse create(TemplateRequest request);

    TemplateResponse update(Long id, TemplateRequest request);

    void delete(Long id);
}
