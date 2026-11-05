package pe.edu.solweb.contracts.dto;

import pe.edu.solweb.contracts.entity.Template;

import java.time.LocalDateTime;

public record TemplateResponse(Long id, Long planId, String planName, String name, String content, LocalDateTime updatedAt) {

    public static TemplateResponse from(Template t) {
        return new TemplateResponse(t.getId(), t.getPlan().getId(), t.getPlan().getName(),
                t.getName(), t.getContent(), t.getUpdatedAt());
    }
}
