package com.stea.controller;

import com.stea.entity.InventairePhysique;
import com.stea.entity.LigneInventaire;
import com.stea.service.InventaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventaires")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class InventaireController {

    private final InventaireService inventaireService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK')")
    public ResponseEntity<InventairePhysique> creer(
            @RequestBody Map<String, Object> body) {
        Long entrepotId = Long.valueOf(body.get("entrepotId").toString());
        InventairePhysique.TypeInventaire type = InventairePhysique.TypeInventaire.valueOf(
                body.getOrDefault("type", "COMPTAGE_COMPLET").toString());
        LocalDate datePrevue = body.get("datePrevue") != null
                ? LocalDate.parse(body.get("datePrevue").toString()) : null;
        String realisateur = (String) body.getOrDefault("realisateur", "");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventaireService.creer(entrepotId, type, datePrevue, realisateur));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK')")
    public ResponseEntity<List<InventairePhysique>> lister() {
        return ResponseEntity.ok(inventaireService.lister());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK')")
    public ResponseEntity<InventairePhysique> get(@PathVariable Long id) {
        return ResponseEntity.ok(inventaireService.get(id));
    }

    @PostMapping("/{id}/demarrer")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK')")
    public ResponseEntity<InventairePhysique> demarrer(@PathVariable Long id) {
        return ResponseEntity.ok(inventaireService.demarrer(id));
    }

    @PutMapping("/lignes/{ligneId}/compter")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK')")
    public ResponseEntity<LigneInventaire> compter(
            @PathVariable Long ligneId, @RequestBody Map<String, Object> body) {
        Integer quantite = Integer.valueOf(body.get("quantitePhysique").toString());
        String observation = (String) body.getOrDefault("observation", "");
        return ResponseEntity.ok(inventaireService.compter(ligneId, quantite, observation));
    }

    @PostMapping("/{id}/valider")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK')")
    public ResponseEntity<InventairePhysique> valider(@PathVariable Long id) {
        return ResponseEntity.ok(inventaireService.valider(id));
    }

    @GetMapping("/{id}/lignes")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK')")
    public ResponseEntity<List<LigneInventaire>> getLignes(@PathVariable Long id) {
        return ResponseEntity.ok(inventaireService.getLignesInventaire(id));
    }

    @GetMapping("/{id}/ecarts")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK')")
    public ResponseEntity<List<LigneInventaire>> getEcarts(@PathVariable Long id) {
        return ResponseEntity.ok(inventaireService.getEcarts(id));
    }
}
