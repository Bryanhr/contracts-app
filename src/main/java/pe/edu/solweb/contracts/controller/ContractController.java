package pe.edu.solweb.contracts.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.solweb.contracts.dto.ContractRequest;
import pe.edu.solweb.contracts.dto.ContractResponse;
import pe.edu.solweb.contracts.entity.Contract;
import pe.edu.solweb.contracts.entity.ContractStatus;
import pe.edu.solweb.contracts.entity.PlanSupportService;
import pe.edu.solweb.contracts.entity.Template;
import pe.edu.solweb.contracts.entity.Validity;
import pe.edu.solweb.contracts.exception.ResourceNotFoundException;
import pe.edu.solweb.contracts.repository.ContractRepository;
import pe.edu.solweb.contracts.repository.PlanSupportServiceRepository;
import pe.edu.solweb.contracts.repository.TemplateRepository;
import pe.edu.solweb.contracts.service.ContractService;
import pe.edu.solweb.contracts.service.PdfService;
import pe.edu.solweb.contracts.service.StorageService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService service;
    private final PdfService pdfService;
    private final StorageService storageService;
    private final ContractRepository contractRepository;
    private final TemplateRepository templateRepository;
    private final PlanSupportServiceRepository planSupportServices;

    @GetMapping
    public List<ContractResponse> list(@RequestParam(required = false) ContractStatus status,
                                       @RequestParam(required = false) Long customerId,
                                       @RequestParam(required = false) Validity validity) {
        return service.search(status, customerId, validity);
    }

    @GetMapping("/{id}")
    public ContractResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContractResponse create(@Valid @RequestBody ContractRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public ContractResponse update(@PathVariable Long id, @Valid @RequestBody ContractRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{id}/pdf")
    @Transactional(readOnly = true)
    public ResponseEntity<InputStreamResource> downloadContractPdf(@PathVariable Long id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrato no encontrado con id: " + id));

        Long planId = contract.getPlan().getId();
        Template template = templateRepository.findByPlanId(planId).stream()
                .max(Comparator.comparing(Template::getUpdatedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .orElseThrow(() -> new ResourceNotFoundException("El plan no tiene plantilla registrada"));
        if (template.getPlan() == null || !planId.equals(template.getPlan().getId())) {
            throw new ResourceNotFoundException("La plantilla no corresponde al plan del contrato");
        }

        List<PlanSupportService> services = planSupportServices.findByPlanId(planId);
        byte[] pdfBytes = pdfService.generateContractPdf(contract, template, services);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=contrato_" + id + ".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(new ByteArrayInputStream(pdfBytes)));
    }

    @GetMapping("/{id}/signed-document")
    public ResponseEntity<InputStreamResource> downloadSignedContract(@PathVariable Long id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrato no encontrado con id: " + id));

        if (contract.getStatus() != ContractStatus.SIGNED) {
            throw new ResourceNotFoundException("El contrato aún no tiene un documento firmado");
        }

        byte[] pdfBytes;
        try (InputStream in = URI.create(storageService.getFileUrl("contract_" + id)).toURL().openStream()) {
            pdfBytes = in.readAllBytes();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Falla al obtener el archivo del servicio externo de almacenamiento");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=contrato_firmado_" + id + ".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(new ByteArrayInputStream(pdfBytes)));
    }

    @PostMapping("/{id}/upload-signed")
    public ResponseEntity<Map<String, Object>> uploadSignedContract(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrato no encontrado con id: " + id));

        if (!"application/pdf".equals(file.getContentType())) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Solo se aceptan archivos PDF");
        }

        String url;
        try {
            url = storageService.storeFile(file, "contract_" + id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Falla al subir el archivo al servicio externo de almacenamiento");
        }

        contract.setStatus(ContractStatus.SIGNED);
        contract.setSignedDate(LocalDate.now());
        contractRepository.save(contract);

        return ResponseEntity.ok(Map.of(
                "contractId", id,
                "status", "SIGNED",
                "pdfUrl", url,
                "message", "Contrato firmado subido y actualizado correctamente."));
    }
}