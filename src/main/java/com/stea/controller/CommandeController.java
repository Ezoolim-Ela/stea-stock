package com.stea.controller;

import com.stea.dto.*;
import com.stea.entity.Utilisateur;
import com.stea.repository.UtilisateurRepository;
import com.stea.service.CommandeService;
import com.stea.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/commandes")
@CrossOrigin
@RequiredArgsConstructor
public class CommandeController {

    private final CommandeService commandeService;
    private final ExportService exportService;
    private final UtilisateurRepository utilisateurRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'COMPTABLE', 'OPERATEUR_STOCK', 'PLANIFICATEUR_TRANSPORT')")
    public ResponseEntity<Page<CommandeResponse>> search(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String typeFlux,
            @RequestParam(required = false) String statut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(commandeService.search(search, typeFlux, statut, page, size));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'COMPTABLE', 'OPERATEUR_STOCK', 'PLANIFICATEUR_TRANSPORT')")
    public ResponseEntity<CommandeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(commandeService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK', 'PLANIFICATEUR_TRANSPORT', 'COMPTABLE')")
    public ResponseEntity<CommandeResponse> create(
            @Valid @RequestBody CommandeRequest request,
            Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Utilisateur user = utilisateurRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouve"));
        return ResponseEntity.ok(commandeService.create(request, user.getId()));
    }

    @PostMapping("/{id}/lignes")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK', 'PLANIFICATEUR_TRANSPORT', 'COMPTABLE')")
    public ResponseEntity<CommandeResponse> addLigne(
            @PathVariable Long id, @Valid @RequestBody LigneCommandeRequest request) {
        return ResponseEntity.ok(commandeService.addLigne(id, request));
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK', 'PLANIFICATEUR_TRANSPORT')")
    public ResponseEntity<CommandeResponse> updateStatut(@PathVariable Long id, @RequestParam String statut) {
        return ResponseEntity.ok(commandeService.updateStatut(id, statut));
    }

    @PatchMapping("/{id}/paiement")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'COMPTABLE')")
    public ResponseEntity<CommandeResponse> enregistrerPaiement(
            @PathVariable Long id, @RequestBody PaiementRequest request) {
        return ResponseEntity.ok(commandeService.enregistrerPaiement(id, request));
    }

    @GetMapping("/stats/repartition")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'COMPTABLE')")
    public ResponseEntity<List<Object[]>> repartitionStatuts() {
        return ResponseEntity.ok(commandeService.getRepartitionStatuts());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'COMPTABLE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commandeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel(HttpServletResponse response) throws java.io.IOException {
        byte[] data = exportService.exportCommandesExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=commandes.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(data.length)
                .body(data);
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf(HttpServletResponse response) throws java.io.IOException {
        byte[] data = exportService.exportCommandesPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=commandes.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(data.length)
                .body(data);
    }
}
