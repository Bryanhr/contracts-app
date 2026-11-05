package pe.edu.solweb.contracts.mappers;

import org.springframework.stereotype.Component;
import pe.edu.solweb.contracts.dto.TemplateRequest;
import pe.edu.solweb.contracts.entity.Plan;
import pe.edu.solweb.contracts.entity.Template;

import java.time.LocalDateTime;

@Component
public class TemplateMapper {

    public Template toEntity(TemplateRequest request, Plan plan) {
        Template template = new Template();
        apply(template, request, plan);
        return template;
    }

    public void update(Template template, TemplateRequest request, Plan plan) {
        apply(template, request, plan);
    }

    private void apply(Template template, TemplateRequest request, Plan plan) {
        template.setPlan(plan);
        template.setName(request.name());
        template.setContent(document(request.content()));
        template.setUpdatedAt(LocalDateTime.now());
    }

    static String document(String content) {
        String text = content == null ? "" : content.strip();
        if (text.contains("# CONTRATO")) {
            return text;
        }
        return HEAD + text + TAIL;
    }

    private static final String HEAD = """
            # CONTRATO DE PRESTACIÓN DE SERVICIOS DE SOPORTE

            N.º {{numero}}

            SERVIT y {{cliente}} firman este contrato el {{fecha_emision}}. {{cliente}}, RUC {{ruc}}{{representante_frase}}.{{contacto}}

            SERVIT prestará el plan "{{plan}}" con los servicios, plazos y tarifas que se indican a continuación.

            ## 1) Servicios del plan.
            {{servicios}}
            """;

    private static final String TAIL = """

            ## 2) Vigencia y tiempo de atención.
            El servicio va del {{fecha_inicio}} al {{fecha_fin}}, por {{duracion}}. El tiempo máximo de atención del plan es de {{tiempo_atencion}} desde que se reporta el incidente.

            ## 3) Condiciones económicas y forma de pago.
            {{cliente}} pagará a SERVIT {{costo_mensual}} al mes por el plan "{{plan}}". En {{duration_months}} meses, el total del periodo es {{total}}. El pago es por mes vencido, dentro de los 15 días de recibido el comprobante.{{monto_registrado}}

            Las partes firman este contrato en la fecha indicada arriba.{{firmado}}""";
}
