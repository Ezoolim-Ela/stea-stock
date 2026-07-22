package com.stea.controller;

import com.stea.dto.*;
import com.stea.service.ExportService;
import com.stea.service.LivraisonService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/livraisons")
@CrossOrigin
@RequiredArgsConstructor
public class LivraisonController {

    private final LivraisonService livraisonService;
    private final ExportService exportService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'PLANIFICATEUR_TRANSPORT', 'OPERATEUR_STOCK', 'COMPTABLE')")
    public ResponseEntity<Page<LivraisonResponse>> search(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String statut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(livraisonService.search(search, statut, page, size));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'PLANIFICATEUR_TRANSPORT', 'COMPTABLE')")
    public ResponseEntity<LivraisonResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(livraisonService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'PLANIFICATEUR_TRANSPORT', 'OPERATEUR_STOCK')")
    public ResponseEntity<LivraisonResponse> create(@Valid @RequestBody LivraisonRequest request) {
        return ResponseEntity.ok(livraisonService.create(request));
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'PLANIFICATEUR_TRANSPORT', 'OPERATEUR_STOCK')")
    public ResponseEntity<LivraisonResponse> updateStatut(@PathVariable Long id, @RequestParam String statut) {
        return ResponseEntity.ok(livraisonService.updateStatut(id, statut));
    }

    @PatchMapping("/{id}/bordereau")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'PLANIFICATEUR_TRANSPORT', 'OPERATEUR_STOCK')")
    public ResponseEntity<LivraisonResponse> transmettreBordereau(@PathVariable Long id) {
        return ResponseEntity.ok(livraisonService.transmettreBordereau(id));
    }

    @PatchMapping("/{id}/facture")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'COMPTABLE')")
    public ResponseEntity<LivraisonResponse> envoyerFacture(@PathVariable Long id) {
        return ResponseEntity.ok(livraisonService.envoyerFacture(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        livraisonService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel(HttpServletResponse response) throws java.io.IOException {
        byte[] data = exportService.exportLivraisonsExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=livraisons.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(data.length)
                .body(data);
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf(HttpServletResponse response) throws java.io.IOException {
        byte[] data = exportService.exportLivraisonsPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=livraisons.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(data.length)
                .body(data);
    }
}
