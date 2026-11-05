package pe.edu.solweb.contracts.service;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import org.junit.jupiter.api.Test;
import pe.edu.solweb.contracts.entity.Contract;
import pe.edu.solweb.contracts.entity.ContractStatus;
import pe.edu.solweb.contracts.entity.Customer;
import pe.edu.solweb.contracts.entity.Plan;
import pe.edu.solweb.contracts.entity.PlanSupportService;
import pe.edu.solweb.contracts.entity.ServiceType;
import pe.edu.solweb.contracts.entity.SupportService;
import pe.edu.solweb.contracts.entity.Template;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfServiceTest {

    private final PdfService pdfService = new PdfService();

    @Test
    void buildsAContractFromThePlanAndItsTemplate() throws IOException {
        Contract contract = contract(null);
        byte[] pdf = pdfService.generateContractPdf(
                contract, template(contract.getPlan()), services(contract.getPlan()));
        Files.createDirectories(Path.of("target"));
        try {
            Files.write(Path.of("target", "contrato-preview.pdf"), pdf);
        } catch (IOException ignored) {
        }

        String text = textOf(pdf);

        assertTrue(text.contains("SERVIT"));
        assertFalse(text.contains("SOLWEB"));
        assertFalse(text.contains("un solo plan"));
        assertFalse(text.contains("El detalle del alcance"));
        assertTrue(text.contains("con los servicios, plazos y tarifas que se indican a continuación"));
        assertFalse(text.contains("soles (PEN)"));
        assertTrue(hasSignatureImage(pdf));
        assertFalse(text.contains("reunidos de una parte"));
        assertTrue(text.contains("firman este contrato el 4 de noviembre de 2026"));
        assertTrue(text.contains("INVERSIONES ANDES S.A.C."));
        assertTrue(text.contains("20123456789"));
        assertTrue(text.contains("Ana Quispe"));
        assertTrue(text.contains("Plan Corporativo"));
        assertTrue(text.contains("Monitoreo de servidores"));
        assertTrue(text.contains("hasta 20 eventos al mes"));
        assertTrue(text.contains("Mesa de ayuda"));
        assertFalse(text.contains("Migración de correo"));
        assertTrue(text.contains("prestará el plan \"Plan Corporativo\""));
        assertTrue(text.contains("1) Servicios del plan."));
        assertTrue(text.contains("48 horas"));
        assertTrue(text.contains("S/ 1,500.00"));
        assertTrue(text.contains("S/ 18,000.00"));
        assertTrue(text.contains("S/ 15.00"));
        assertFalse(text.contains("$"));
        assertTrue(text.contains("Por SERVIT:"));
        assertFalse(text.contains("{{cliente}}"));
        assertFalse(text.contains("GENERATED"));
        assertFalse(text.contains("Firmado el"));
    }

    @Test
    void keepsTheSeedTemplateTextAndFillsItsMarkers() throws IOException {
        Contract contract = contract(null);
        Template template = template(contract.getPlan());
        template.setContent(document("""
                Se acuerda la prestación de soporte técnico por {{duracion}} con un costo mensual de {{costo_mensual}} y un tiempo de respuesta máximo de {{tiempo_atencion}}.
                """));

        String text = textOf(pdfService.generateContractPdf(contract, template, List.of()));

        assertTrue(text.contains("CONTRATO DE PRESTACIÓN DE SERVICIOS DE SOPORTE"));
        assertFalse(text.contains("CONTRATO DE SERVICIOS DE SOPORTE - PLAN BÁSICO"));
        assertFalse(text.contains("Entre INVERSIONES ANDES S.A.C. (RUC 20123456789)"));
        assertEquals(1, count(text, "20123456789"));
        assertTrue(text.contains("Se acuerda la prestación de soporte técnico por 12 meses"));
        assertTrue(text.contains("S/ 1,500.00"));
        assertTrue(text.contains("48 horas"));
        assertFalse(text.contains("{{"));
        assertFalse(text.contains("meses meses"));
        assertFalse(text.contains("S/ S/"));
    }

    @Test
    void mentionsTheSignatureDateWhenTheContractIsAlreadySigned() throws IOException {
        Contract contract = contract(LocalDate.of(2026, 11, 10));
        String text = textOf(pdfService.generateContractPdf(
                contract, template(contract.getPlan()), List.of()));

        assertTrue(text.contains("Firmado el 10 de noviembre de 2026"));
    }

    @Test
    void rejectsATemplateThatBelongsToAnotherPlan() {
        Contract contract = contract(null);
        Plan other = new Plan();
        other.setId(99L);
        other.setName("Plan Básico");

        assertThrows(IllegalStateException.class, () -> pdfService.generateContractPdf(
                contract, template(other), services(contract.getPlan())));
    }

    @Test
    void pdfBytesStartWithThePdfHeader() {
        Contract contract = contract(null);
        byte[] pdf = pdfService.generateContractPdf(contract, template(contract.getPlan()), List.of());

        assertTrue(pdf.length > 5);
        assertTrue(new String(pdf, 0, 5, StandardCharsets.US_ASCII).startsWith("%PDF"));
    }

    private static boolean hasSignatureImage(byte[] pdf) throws IOException {
        PdfReader reader = new PdfReader(pdf);
        try {
            PdfDictionary resources = reader.getPageN(1).getAsDict(PdfName.RESOURCES);
            PdfDictionary images = resources == null ? null : resources.getAsDict(PdfName.XOBJECT);
            return images != null && !images.getKeys().isEmpty();
        } finally {
            reader.close();
        }
    }

    private static int count(String text, String piece) {
        int found = 0;
        int from = 0;
        while ((from = text.indexOf(piece, from)) >= 0) {
            found++;
            from += piece.length();
        }
        return found;
    }

    private static String textOf(byte[] pdf) throws IOException {
        PdfReader reader = new PdfReader(pdf);
        try {
            PdfTextExtractor extractor = new PdfTextExtractor(reader);
            StringBuilder text = new StringBuilder();
            for (int page = 1; page <= reader.getNumberOfPages(); page++) {
                text.append(extractor.getTextFromPage(page)).append('\n');
            }
            return text.toString().replaceAll("\\s+", " ");
        } finally {
            reader.close();
        }
    }

    private static Contract contract(LocalDate signedDate) {
        Customer customer = new Customer();
        customer.setBusinessName("INVERSIONES ANDES S.A.C.");
        customer.setRuc("20123456789");
        customer.setRepresentative("Ana Quispe");
        customer.setEmail("ana@andes.pe");
        customer.setPhone("999888777");
        customer.setAddress("Av. Arequipa 123, Lima");

        Plan plan = new Plan();
        plan.setId(3L);
        plan.setName("Plan Corporativo");
        plan.setResponseTimeHours(48);
        plan.setMonthlyCost(new BigDecimal("1500.00"));
        plan.setDurationMonths(12);

        Contract contract = new Contract();
        contract.setId(12L);
        contract.setCustomer(customer);
        contract.setPlan(plan);
        contract.setIssueDate(LocalDate.of(2026, 11, 4));
        contract.setStartDate(LocalDate.of(2026, 11, 1));
        contract.setEndDate(LocalDate.of(2027, 11, 1));
        contract.setAmount(new BigDecimal("1500.00"));
        contract.setStatus(ContractStatus.GENERATED);
        contract.setSignedDate(signedDate);
        return contract;
    }

    private static Template template(Plan plan) {
        Template template = new Template();
        template.setPlan(plan);
        template.setName("Soporte corporativo");
        template.setContent(document("""
                Se acuerda la prestación de soporte técnico por {{duracion}} con un costo mensual de {{costo_mensual}} y un tiempo de respuesta máximo de {{tiempo_atencion}}.
                """));
        return template;
    }

    private static String document(String clause) {
        return """
                # CONTRATO DE PRESTACIÓN DE SERVICIOS DE SOPORTE

                N.º {{numero}}

                SERVIT y {{cliente}} firman este contrato el {{fecha_emision}}. {{cliente}}, RUC {{ruc}}{{representante_frase}}.{{contacto}}

                SERVIT prestará el plan "{{plan}}" con los servicios, plazos y tarifas que se indican a continuación.

                ## 1) Servicios del plan.
                {{servicios}}
                %s
                ## 2) Vigencia y tiempo de atención.
                El servicio va del {{fecha_inicio}} al {{fecha_fin}}, por {{duracion}}. El tiempo máximo de atención del plan es de {{tiempo_atencion}} desde que se reporta el incidente.

                ## 3) Condiciones económicas y forma de pago.
                {{cliente}} pagará a SERVIT {{costo_mensual}} al mes por el plan "{{plan}}". En {{duration_months}} meses, el total del periodo es {{total}}. El pago es por mes vencido, dentro de los 15 días de recibido el comprobante.{{monto_registrado}}

                Las partes firman este contrato en la fecha indicada arriba.{{firmado}}
                """.formatted(clause.strip() + "\n");
    }

    private static List<PlanSupportService> services(Plan plan) {
        Plan other = new Plan();
        other.setId(9L);
        other.setName("Plan Ajeno");
        return List.of(
                link(plan, "Monitoreo de servidores", "Infraestructura", "eventos", 20, "15.00"),
                link(plan, "Mesa de ayuda", "Atención", "tickets", 40, null),
                link(other, "Migración de correo", "Proyecto", "casos", 1, "80.00"));
    }

    private static PlanSupportService link(Plan plan, String name, String typeName, String unit,
                                           int monthlyLimit, String extraCost) {
        ServiceType type = new ServiceType();
        type.setName(typeName);
        SupportService service = new SupportService();
        service.setName(name);
        service.setServiceType(type);
        service.setUnit(unit);
        PlanSupportService link = new PlanSupportService();
        link.setPlan(plan);
        link.setSupportService(service);
        link.setMonthlyLimit(monthlyLimit);
        if (extraCost != null) {
            link.setExtraCost(new BigDecimal(extraCost));
        }
        return link;
    }
}
