package pe.edu.solweb.contracts.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import pe.edu.solweb.contracts.entity.Contract;
import pe.edu.solweb.contracts.entity.Customer;
import pe.edu.solweb.contracts.entity.Plan;
import pe.edu.solweb.contracts.entity.PlanSupportService;
import pe.edu.solweb.contracts.entity.SupportService;
import pe.edu.solweb.contracts.entity.Template;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class PdfService {

    private static final String PROVIDER = "SERVIT";
    private static final String PROVIDER_LINE = "Servicios de soporte";

    private static final DateTimeFormatter LONG_DATE =
            DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale.of("es", "PE"));

    private static final Font BRAND = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
    private static final Font BRAND_LINE = FontFactory.getFont(FontFactory.HELVETICA, 9);
    private static final Font TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
    private static final Font ITEM = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
    private static final Font TEXT = FontFactory.getFont(FontFactory.HELVETICA, 11);
    private static final Font SMALL = FontFactory.getFont(FontFactory.HELVETICA, 10);
    private static final Font SIGN_LABEL = FontFactory.getFont(FontFactory.HELVETICA, 11);

    public byte[] generateContractPdf(Contract contract, Template template, List<PlanSupportService> planServices) {
        if (template == null) {
            throw new IllegalStateException("El plan del contrato no tiene una plantilla registrada");
        }
        requireTemplateOfPlan(contract, template);

        Customer customer = contract.getCustomer();
        List<PlanSupportService> services = planServices == null ? List.of() : planServices;

        Document document = new Document(PageSize.A4, 64, 64, 52, 56);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.addTitle("Contrato de prestación de servicios N.º " + contractNumber(contract));
            document.addAuthor(PROVIDER);
            document.open();

            document.add(letterhead(contract));
            render(document, fill(template.getContent(), contract, services));
            document.add(signatures(customer));
            document.close();
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Error al generar el PDF del contrato", e);
        }

        return out.toByteArray();
    }

    private void requireTemplateOfPlan(Contract contract, Template template) {
        Plan contractPlan = contract.getPlan();
        Plan templatePlan = template.getPlan();
        if (contractPlan == null || templatePlan == null) {
            throw new IllegalStateException("La plantilla no corresponde al plan del contrato");
        }
        if (contractPlan.getId() != null && templatePlan.getId() != null
                && !contractPlan.getId().equals(templatePlan.getId())) {
            throw new IllegalStateException("La plantilla no corresponde al plan del contrato");
        }
        if (contractPlan.getId() == null && templatePlan != contractPlan) {
            throw new IllegalStateException("La plantilla no corresponde al plan del contrato");
        }
    }

    private void render(Document document, String content) throws DocumentException {
        for (String raw : content.split("\\R")) {
            String line = raw.trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.startsWith("# ")) {
                Paragraph title = new Paragraph(line.substring(2).trim(), TITLE);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingBefore(18);
                title.setSpacingAfter(4);
                document.add(title);
            } else if (line.startsWith("## ")) {
                document.add(heading(line.substring(3).trim()));
            } else if (line.startsWith("N.º ") || line.startsWith("N.° ")) {
                Paragraph number = new Paragraph(line, SMALL);
                number.setAlignment(Element.ALIGN_CENTER);
                number.setSpacingAfter(14);
                document.add(number);
            } else {
                body(document, line);
            }
        }
    }

    private String servicesBlock(Plan plan, List<PlanSupportService> planServices) {
        List<PlanSupportService> own = planServices.stream()
                .filter(link -> belongsToPlan(link, plan))
                .toList();
        if (own.isEmpty()) {
            return "El plan \"" + nz(plan.getName()) + "\" no tiene servicios registrados.";
        }
        StringBuilder block = new StringBuilder("Servicios incluidos en el plan \"")
                .append(nz(plan.getName()))
                .append("\":");
        for (PlanSupportService link : own) {
            block.append("\n- ").append(serviceLine(link));
        }
        return block.toString();
    }

    private boolean belongsToPlan(PlanSupportService link, Plan plan) {
        if (link.getPlan() == null || link.getSupportService() == null || plan == null) {
            return false;
        }
        if (plan.getId() != null && link.getPlan().getId() != null) {
            return plan.getId().equals(link.getPlan().getId());
        }
        return link.getPlan() == plan;
    }

    private String serviceLine(PlanSupportService link) {
        SupportService service = link.getSupportService();
        StringBuilder line = new StringBuilder(nz(service.getName()));
        if (service.getServiceType() != null && present(service.getServiceType().getName())) {
            line.append(" (").append(service.getServiceType().getName().trim()).append(")");
        }
        if (link.getMonthlyLimit() != null) {
            line.append(": hasta ").append(link.getMonthlyLimit());
            if (present(service.getUnit())) {
                line.append(" ").append(service.getUnit().trim());
            }
            line.append(" al mes");
        }
        if (link.getExtraCost() != null) {
            line.append(". Costo extra ").append(money(link.getExtraCost()));
        }
        return line.toString();
    }

    private String fill(String content, Contract contract, List<PlanSupportService> services) {
        String text = content == null ? "" : content;
        Customer customer = contract.getCustomer();
        Plan plan = contract.getPlan();
        return text
                .replace("{{servicios}}", servicesBlock(plan, services))
                .replace("{{cliente}}", nz(customer.getBusinessName()))
                .replace("{{business_name}}", nz(customer.getBusinessName()))
                .replace("{{ruc}}", nz(customer.getRuc()))
                .replace("{{representante_frase}}", representativePhrase(customer))
                .replace("{{representante}}", nz(customer.getRepresentative()))
                .replace("{{contacto}}", contactSentence(customer))
                .replace("{{direccion}}", nz(customer.getAddress()))
                .replace("{{correo}}", nz(customer.getEmail()))
                .replace("{{telefono}}", nz(customer.getPhone()))
                .replace("{{plan}}", nz(plan.getName()))
                .replace("{{numero}}", contractNumber(contract))
                .replace("{{tiempo_atencion}}", nz(plan.getResponseTimeHours()) + " horas")
                .replace("{{response_time_hours}}", nz(plan.getResponseTimeHours()))
                .replace("{{costo_mensual}}", money(plan.getMonthlyCost()))
                .replace("{{monthly_cost}}", amount(plan.getMonthlyCost()))
                .replace("{{duracion}}", nz(plan.getDurationMonths()) + " meses")
                .replace("{{duration_months}}", nz(plan.getDurationMonths()))
                .replace("{{total}}", termTotal(plan))
                .replace("{{monto_registrado}}", registeredAmount(contract, plan))
                .replace("{{monto}}", money(contract.getAmount()))
                .replace("{{fecha_inicio}}", date(contract.getStartDate()))
                .replace("{{fecha_fin}}", date(contract.getEndDate()))
                .replace("{{fecha_emision}}", date(contract.getIssueDate()))
                .replace("{{firmado}}", signedSentence(contract));
    }

    private String representativePhrase(Customer customer) {
        if (!present(customer.getRepresentative())) {
            return "";
        }
        return ", participa a través de " + customer.getRepresentative().trim();
    }

    private String contactSentence(Customer customer) {
        StringBuilder contact = new StringBuilder();
        appendLabeled(contact, "Domicilio", customer.getAddress());
        appendLabeled(contact, "Correo", customer.getEmail());
        appendLabeled(contact, "Teléfono", customer.getPhone());
        return contact.isEmpty() ? "" : " " + contact;
    }

    private String signedSentence(Contract contract) {
        return contract.getSignedDate() == null ? "" : " Firmado el " + date(contract.getSignedDate()) + ".";
    }

    private String registeredAmount(Contract contract, Plan plan) {
        if (contract.getAmount() == null || plan.getMonthlyCost() == null
                || contract.getAmount().compareTo(plan.getMonthlyCost()) == 0) {
            return "";
        }
        return " El monto registrado en este contrato es de " + money(contract.getAmount()) + ".";
    }

    private PdfPTable letterhead(Contract contract) throws DocumentException {
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{62, 38});

        Paragraph brand = new Paragraph();
        brand.setLeading(14);
        brand.add(new Phrase(PROVIDER + "\n", BRAND));
        brand.add(new Phrase(PROVIDER_LINE, BRAND_LINE));
        PdfPCell left = new PdfPCell(brand);
        left.setBorder(Rectangle.NO_BORDER);
        left.setPadding(0);
        header.addCell(left);

        Paragraph when = new Paragraph(date(contract.getIssueDate()), TEXT);
        when.setAlignment(Element.ALIGN_RIGHT);
        PdfPCell right = new PdfPCell(when);
        right.setBorder(Rectangle.NO_BORDER);
        right.setHorizontalAlignment(Element.ALIGN_RIGHT);
        right.setVerticalAlignment(Element.ALIGN_TOP);
        right.setPadding(0);
        header.addCell(right);
        return header;
    }

    private PdfPTable signatures(Customer customer) throws DocumentException, IOException {
        PdfPTable firmas = new PdfPTable(3);
        firmas.setWidthPercentage(100);
        firmas.setWidths(new float[]{46, 8, 46});
        firmas.setSpacingBefore(28);
        firmas.addCell(signatureCell("Por el cliente:", clientSigner(customer), nz(customer.getBusinessName()), null));
        PdfPCell gap = new PdfPCell(new Phrase(""));
        gap.setBorder(Rectangle.NO_BORDER);
        firmas.addCell(gap);
        firmas.addCell(signatureCell("Por " + PROVIDER + ":", PROVIDER, PROVIDER_LINE, companySignature()));
        return firmas;
    }

    private Image companySignature() throws DocumentException, IOException {
        java.net.URL resource = getClass().getResource("/signatures/servit-signature.png");
        if (resource == null) {
            throw new IllegalStateException("No está la firma de " + PROVIDER);
        }
        Image image = Image.getInstance(resource);
        image.scaleToFit(150, 46);
        image.setAlignment(Image.ALIGN_LEFT);
        return image;
    }

    private String clientSigner(Customer customer) {
        if (present(customer.getRepresentative())) {
            return customer.getRepresentative().trim();
        }
        return nz(customer.getBusinessName());
    }

    private PdfPCell signatureCell(String role, String signer, String detail, Image signature) {
        PdfPTable stack = new PdfPTable(1);
        stack.setWidthPercentage(100);

        PdfPCell label = new PdfPCell(new Phrase(role, SIGN_LABEL));
        label.setBorder(Rectangle.NO_BORDER);
        label.setPadding(0);
        label.setPaddingBottom(4);
        stack.addCell(label);

        PdfPCell signSpace = signature == null
                ? new PdfPCell(new Phrase(""))
                : new PdfPCell(signature, false);
        signSpace.setBorder(Rectangle.NO_BORDER);
        signSpace.setPadding(0);
        signSpace.setFixedHeight(48);
        signSpace.setVerticalAlignment(Element.ALIGN_BOTTOM);
        signSpace.setHorizontalAlignment(Element.ALIGN_LEFT);
        stack.addCell(signSpace);

        Paragraph name = new Paragraph();
        name.setLeading(13);
        name.add(new Phrase(signer + "\n", TEXT));
        name.add(new Phrase(detail, SMALL));
        PdfPCell signed = new PdfPCell(name);
        signed.setBorder(Rectangle.TOP);
        signed.setBorderWidthTop(0.7f);
        signed.setPaddingTop(6);
        signed.setPaddingLeft(0);
        signed.setPaddingRight(8);
        stack.addCell(signed);

        PdfPCell wrap = new PdfPCell(stack);
        wrap.setBorder(Rectangle.NO_BORDER);
        wrap.setPadding(0);
        return wrap;
    }

    private Paragraph heading(String title) {
        Paragraph heading = new Paragraph(title, ITEM);
        heading.setSpacingBefore(10);
        heading.setSpacingAfter(2);
        return heading;
    }

    private void body(Document document, String text) throws DocumentException {
        Paragraph paragraph = new Paragraph(text, TEXT);
        paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
        paragraph.setLeading(15);
        paragraph.setSpacingAfter(2);
        document.add(paragraph);
    }

    private String contractNumber(Contract contract) {
        long id = contract.getId() == null ? 0 : contract.getId();
        int year = contract.getIssueDate() == null
                ? LocalDate.now().getYear()
                : contract.getIssueDate().getYear();
        return String.format("%05d-%d", id, year);
    }

    private String termTotal(Plan plan) {
        if (plan.getMonthlyCost() == null || plan.getDurationMonths() == null) {
            return "-";
        }
        return money(plan.getMonthlyCost().multiply(BigDecimal.valueOf(plan.getDurationMonths())));
    }

    private String money(BigDecimal value) {
        String amount = amount(value);
        return "-".equals(amount) ? amount : "S/ " + amount;
    }

    private String amount(BigDecimal value) {
        if (value == null) {
            return "-";
        }
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        return new DecimalFormat("#,##0.00", symbols).format(value);
    }

    private String date(LocalDate value) {
        return value == null ? "-" : LONG_DATE.format(value);
    }

    private void appendLabeled(StringBuilder text, String label, String value) {
        if (!present(value)) {
            return;
        }
        if (!text.isEmpty()) {
            text.append(" ");
        }
        text.append(label).append(": ").append(value.trim()).append(".");
    }

    private boolean present(String value) {
        return value != null && !value.isBlank();
    }

    private String nz(Object value) {
        if (value == null) {
            return "-";
        }
        String text = value.toString().trim();
        return text.isEmpty() ? "-" : text;
    }
}
