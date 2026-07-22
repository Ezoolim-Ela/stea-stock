package com.stea.controller;

import com.stea.dto.*;
import com.stea.service.FournisseurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fournisseurs")
@CrossOrigin
@RequiredArgsConstructor
public class FournisseurController {

    private final FournisseurService fournisseurService;

    @GetMapping
    public ResponseEntity<Page<FournisseurResponse>> search(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String statut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(fournisseurService.search(search, statut, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FournisseurResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(fournisseurService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK')")
    public ResponseEntity<FournisseurResponse> create(@Valid @RequestBody FournisseurRequest request) {
        return ResponseEntity.ok(fournisseurService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK')")
    public ResponseEntity<FournisseurResponse> update(@PathVariable Long id, @Valid @RequestBody FournisseurRequest request) {
        return ResponseEntity.ok(fournisseurService.update(id, request));
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK')")
    public ResponseEntity<FournisseurResponse> updateStatut(@PathVariable Long id, @RequestParam String statut) {
        return ResponseEntity.ok(fournisseurService.updateStatut(id, statut));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fournisseurService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
