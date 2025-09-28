package com.valuation.demo.controller;

import com.valuation.demo.model.Valuation;
import com.valuation.demo.service.ValuationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Controller that provides preview and PDF generation for Valuation reports.
 */
@Controller
@RequestMapping("/api/valuations")
public class ValuationReportController {

    private static final Logger log = LoggerFactory.getLogger(ValuationReportController.class);

    private final ValuationService valuationService;
    private final SpringTemplateEngine templateEngine;

    public ValuationReportController(ValuationService valuationService, SpringTemplateEngine templateEngine) {
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
        model.addAttribute("forPdf", false); // <-- tell template it's preview, not PDF
        return "valuation-template"; // Thymeleaf template name
    }

    // PDF download endpoint
    @GetMapping("/{id}/pdf")
    public ResponseEntity<?> downloadPdf(@PathVariable Long id) {
        Valuation val = valuationService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Valuation not found"));

        // prepare thymeleaf context
        Context ctx = new Context();
        ctx.setVariable("valuation", val);
        ctx.setVariable("createdAt", val.getCreatedAt() != null ? val.getCreatedAt().toString() : "");
        ctx.setVariable("generationDate", LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        ctx.setVariable("forPdf", true); // <-- tell template it's for PDF generation

        // Render HTML
        String html = templateEngine.process("valuation-template", ctx);

        // --- DEBUG: write rendered HTML to tmp so you can inspect it if needed ---
        try {
            java.nio.file.Path debugPath = java.nio.file.Paths.get(System.getProperty("java.io.tmpdir"),
                    "valuation-" + id + "-html-debug.html");
            java.nio.file.Files.writeString(debugPath, html, java.nio.charset.StandardCharsets.UTF_8);
            log.info("Rendered HTML written to {}", debugPath.toAbsolutePath());
        } catch (Exception e) {
            log.warn("Failed to write debug HTML file: {}", e.getMessage());
        }

        // --- SANITIZE: escape bare ampersands not part of entities to avoid XML parse errors ---
        String sanitized = html.replaceAll("&(?!#?\\w+;)", "&amp;");

        // write sanitized HTML for inspection
        try {
            java.nio.file.Path debugSanitized = java.nio.file.Paths.get(System.getProperty("java.io.tmpdir"),
                    "valuation-" + id + "-html-sanitized-debug.html");
            java.nio.file.Files.writeString(debugSanitized, sanitized, java.nio.charset.StandardCharsets.UTF_8);
            log.info("Sanitized HTML written to {}", debugSanitized.toAbsolutePath());
        } catch (Exception e) {
            log.warn("Failed to write sanitized debug HTML file: {}", e.getMessage());
        }

        // Convert sanitized HTML -> PDF using OpenHTMLToPDF
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            com.openhtmltopdf.pdfboxout.PdfRendererBuilder builder =
                    new com.openhtmltopdf.pdfboxout.PdfRendererBuilder();

            // IMPORTANT: set baseUri so relative links (CSS, images) resolve correctly.
            String baseUri = "http://localhost:8080/"; // change if your server/host is different
            builder.withHtmlContent(sanitized, baseUri);

            // Optional: register font files if you need custom fonts, e.g.:
            // builder.useFont(new File("/absolute/path/to/font.ttf"), "MyFont");

            builder.toStream(os);
            builder.run();

            byte[] pdfBytes = os.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename("valuation-" + id + ".pdf").build());
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception ex) {
            // Log full stack trace
            log.error("PDF generation failed for valuation id {}: {}", id, ex.getMessage(), ex);

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("error", "PDF generation failed");
            body.put("message", ex.getMessage());
            body.put("id", id);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body);
        } finally {
            try {
                os.close();
            } catch (IOException ignored) {
            }
        }
    }
}
