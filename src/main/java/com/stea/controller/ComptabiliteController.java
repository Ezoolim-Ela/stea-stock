package com.stea.controller;

import com.stea.entity.Commande;
import com.stea.entity.Livraison;
import com.stea.entity.Utilisateur;
import com.stea.repository.UtilisateurRepository;
import com.stea.service.ComptabiliteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comptabilite")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class ComptabiliteController {

    private final ComptabiliteService comptabiliteService;
    private final UtilisateurRepository utilisateurRepository;

    @GetMapping("/commandes/non-payees")
    @PreAuthorize("hasRole('COMPTABLE')")
    public ResponseEntity<List<Commande>> getCommandesNonPayees() {
        return ResponseEntity.ok(comptabiliteService.getCommandesNonPayees());
    }

    @GetMapping("/commandes/partiel")
    @PreAuthorize("hasRole('COMPTABLE')")
    public ResponseEntity<List<Commande>> getCommandesPartiel() {
        return ResponseEntity.ok(comptabiliteService.getCommandesPayeesPartiellement());
    }

    @GetMapping("/commandes/payees")
    @PreAuthorize("hasRole('COMPTABLE')")
    public ResponseEntity<List<Commande>> getCommandesPayees() {
        return ResponseEntity.ok(comptabiliteService.getCommandesPayees());
    }

    @GetMapping("/livraisons/en-attente")
    @PreAuthorize("hasRole('COMPTABLE')")
    public ResponseEntity<List<Livraison>> getLivraisonsEnAttente() {
        return ResponseEntity.ok(comptabiliteService.getLivraisonsEnAttentePaiement());
    }

    @PostMapping("/commandes/{id}/valider-paiement")
    @PreAuthorize("hasRole('COMPTABLE')")
    public ResponseEntity<Commande> validerPaiementCommande(
            @PathVariable Long id,
            @RequestParam(required = false) String observation,
            Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Utilisateur user = utilisateurRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouve"));
        return ResponseEntity.ok(comptabiliteService.validerPaiementCommande(id, user.getId(), observation));
    }

    @PostMapping("/livraisons/{id}/valider-paiement")
    @PreAuthorize("hasRole('COMPTABLE')")
    public ResponseEntity<Livraison> validerPaiementLivraison(
            @PathVariable Long id,
            @RequestParam(required = false) String observation,
            Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Utilisateur user = utilisateurRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouve"));
        return ResponseEntity.ok(comptabiliteService.validerPaiementLivraison(id, user.getId(), observation));
    }

    @PostMapping("/livraisons/{id}/refuser-paiement")
    @PreAuthorize("hasRole('COMPTABLE')")
    public ResponseEntity<Livraison> refuserPaiementLivraison(
            @PathVariable Long id,
            @RequestParam(required = false) String observation,
            Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Utilisateur user = utilisateurRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouve"));
        return ResponseEntity.ok(comptabiliteService.refuserPaiementLivraison(id, user.getId(), observation));
    }
}
