package com.valuation.demo.controller;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.valuation.demo.model.Valuation;
import com.valuation.demo.service.ValuationService;

@Controller
@RequestMapping("/api/valuations")
public class ValuationReportController {

    private final ValuationService valuationService;
    private final SpringTemplateEngine templateEngine;

    public ValuationReportController(ValuationService valuationService,
                                     SpringTemplateEngine templateEngine) {
        this.valuationService = valuationService;
        this.templateEngine = templateEngine;
    }

    // HTML preview -> returns Thymeleaf view (rendered HTML)
    @GetMapping("/{id}/preview")
    public String preview(@PathVariable Long id, Model model) {
        Valuation val = valuationService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Valuation not found"));
        model.addAttribute("valuation", val);
        model.addAttribute("createdAt", val.getCreatedAt() != null ? val.getCreatedAt().toString() : "");
        model.addAttribute("generationDate", LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        return "valuation-template"; // Thymeleaf template name
    }

    // PDF download endpoint
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        Valuation val = valuationService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Valuation not found"));

        // Prepare Thymeleaf context
        Context ctx = new Context();
        ctx.setVariable("valuation", val);
        ctx.setVariable("createdAt", val.getCreatedAt() != null ? val.getCreatedAt().toString() : "");
        ctx.setVariable("generationDate", LocalDate.now().format(DateTimeFormatter.ISO_DATE));

        // Render HTML
        String html = templateEngine.process("valuation-template", ctx);

        // Convert to PDF
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            com.openhtmltopdf.pdfboxout.PdfRendererBuilder builder =
                    new com.openhtmltopdf.pdfboxout.PdfRendererBuilder();
            // If you reference CSS/images using relative paths in template, set baseUri to "http://localhost:8080/"
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "PDF generation failed", e);
        }

        byte[] pdf = os.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("valuation-" + id + ".pdf").build());
        headers.setContentLength(pdf.length);

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}
