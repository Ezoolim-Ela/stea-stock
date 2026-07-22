package com.stea.controller;

import com.stea.dto.CategorieRequest;
import com.stea.entity.Categorie;
import com.stea.service.CategorieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class CategorieController {

    private final CategorieService categorieService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK')")
    public ResponseEntity<Categorie> creer(@Valid @RequestBody CategorieRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categorieService.creerCategorie(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK')")
    public ResponseEntity<List<Categorie>> lister() {
        return ResponseEntity.ok(categorieService.listerCategories());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK')")
    public ResponseEntity<Categorie> get(@PathVariable Long id) {
        return ResponseEntity.ok(categorieService.getCategorie(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK')")
    public ResponseEntity<Categorie> modifier(@PathVariable Long id, @Valid @RequestBody CategorieRequest request) {
        return ResponseEntity.ok(categorieService.modifierCategorie(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        categorieService.supprimerCategorie(id);
        return ResponseEntity.noContent().build();
    }
}
