package com.stea.controller;

import com.stea.entity.Entrepot;
import com.stea.entity.Emplacement;
import com.stea.service.EntrepotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entrepots")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class EntrepotController {

    private final EntrepotService entrepotService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<Entrepot> creer(@RequestBody Entrepot entrepot) {
        return ResponseEntity.status(HttpStatus.CREATED).body(entrepotService.creer(entrepot));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK')")
    public ResponseEntity<List<Entrepot>> lister() {
        return ResponseEntity.ok(entrepotService.lister());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK')")
    public ResponseEntity<Entrepot> get(@PathVariable Long id) {
        return ResponseEntity.ok(entrepotService.get(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<Entrepot> modifier(@PathVariable Long id, @RequestBody Entrepot entrepot) {
        return ResponseEntity.ok(entrepotService.modifier(id, entrepot));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        entrepotService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    // === EMPLACEMENTS ===

    @GetMapping("/{entrepotId}/emplacements")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK')")
    public ResponseEntity<List<Emplacement>> listerEmplacements(@PathVariable Long entrepotId) {
        return ResponseEntity.ok(entrepotService.listerEmplacements(entrepotId));
    }

    @GetMapping("/{entrepotId}/emplacements/disponibles")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK')")
    public ResponseEntity<List<Emplacement>> emplacementsDisponibles(@PathVariable Long entrepotId) {
        return ResponseEntity.ok(entrepotService.listerEmplacementsDisponibles(entrepotId));
    }

    @PostMapping("/{entrepotId}/emplacements")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<Emplacement> creerEmplacement(
            @PathVariable Long entrepotId, @RequestBody Emplacement emplacement) {
        emplacement.setEntrepot(Entrepot.builder().id(entrepotId).build());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(entrepotService.creerEmplacement(emplacement));
    }

    @PutMapping("/emplacements/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<Emplacement> modifierEmplacement(
            @PathVariable Long id, @RequestBody Emplacement emplacement) {
        return ResponseEntity.ok(entrepotService.modifierEmplacement(id, emplacement));
    }

    @DeleteMapping("/emplacements/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<Void> supprimerEmplacement(@PathVariable Long id) {
        entrepotService.supprimerEmplacement(id);
        return ResponseEntity.noContent().build();
    }
}
