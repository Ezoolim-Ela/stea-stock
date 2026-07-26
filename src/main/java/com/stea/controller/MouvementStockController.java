package com.stea.controller;

import com.stea.dto.*;
import com.stea.entity.Utilisateur;
import com.stea.repository.UtilisateurRepository;
import com.stea.service.ExportService;
import com.stea.service.MouvementStockService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/mouvements-stock")
@CrossOrigin
@RequiredArgsConstructor
public class MouvementStockController {

    private final MouvementStockService mouvementStockService;
    private final ExportService exportService;
    private final UtilisateurRepository utilisateurRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK', 'COMPTABLE')")
    public ResponseEntity<Page<MouvementStockResponse>> search(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long articleId,
            @RequestParam(required = false) LocalDateTime dateDebut,
            @RequestParam(required = false) LocalDateTime dateFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(mouvementStockService.search(type, articleId, dateDebut, dateFin, page, size));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK')")
    public ResponseEntity<MouvementStockResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(mouvementStockService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK')")
    public ResponseEntity<MouvementStockResponse> create(
            @Valid @RequestBody MouvementStockRequest request,
            Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Utilisateur user = utilisateurRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouve"));
        return ResponseEntity.ok(mouvementStockService.create(request, user.getId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK')")
    public ResponseEntity<MouvementStockResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody MouvementStockRequest request) {
        return ResponseEntity.ok(mouvementStockService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mouvementStockService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel(HttpServletResponse response) throws java.io.IOException {
        byte[] data = exportService.exportMouvementsExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=mouvements_stock.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(data.length)
                .body(data);
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf(HttpServletResponse response) throws java.io.IOException {
        byte[] data = exportService.exportMouvementsPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=mouvements_stock.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(data.length)
                .body(data);
    }
}
