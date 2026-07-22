package com.stea.controller;

import com.stea.entity.Lot;
import com.stea.entity.NumeroSerie;
import com.stea.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stocks-avances")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class StockAvanceController {

    private final StockService stockService;

    // === LOTS ===

    @PostMapping("/lots")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK')")
    public ResponseEntity<Lot> creerLot(@RequestBody Map<String, Object> body) {
        Lot lot = stockService.creerLot(
                Long.valueOf(body.get("articleId").toString()),
                (String) body.get("numeroLot"),
                Integer.valueOf(body.get("quantite").toString()),
                body.get("dateProduction") != null ? LocalDate.parse(body.get("dateProduction").toString()) : null,
                body.get("dateExpiration") != null ? LocalDate.parse(body.get("dateExpiration").toString()) : null,
                body.get("coutUnitaire") != null ? new BigDecimal(body.get("coutUnitaire").toString()) : null,
                (String) body.getOrDefault("fournisseur", "")
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(lot);
    }

    @GetMapping("/lots/article/{articleId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK')")
    public ResponseEntity<List<Lot>> lotsArticle(@PathVariable Long articleId) {
        return ResponseEntity.ok(stockService.getLotsDisponibles(articleId));
    }

    @GetMapping("/lots/expires")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK')")
    public ResponseEntity<List<Lot>> lotsExpires() {
        return ResponseEntity.ok(stockService.getLotsExpires());
    }

    @GetMapping("/lots/expiration-proche")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK')")
    public ResponseEntity<List<Lot>> lotsExpirationProche(
            @RequestParam(defaultValue = "30") int jours) {
        return ResponseEntity.ok(stockService.getLotsExpirationProche(jours));
    }

    // === NUMEROS DE SERIE ===

    @GetMapping("/series/article/{articleId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK')")
    public ResponseEntity<List<NumeroSerie>> seriesArticle(@PathVariable Long articleId) {
        return ResponseEntity.ok(stockService.getNumerosSerieArticle(articleId));
    }

    // === VALORISATION ===

    @GetMapping("/valorisation/{articleId}/fifo")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK', 'COMPTABLE')")
    public ResponseEntity<BigDecimal> valorisationFIFO(@PathVariable Long articleId) {
        return ResponseEntity.ok(stockService.calculerValorisationFIFO(articleId));
    }

    @GetMapping("/valorisation/{articleId}/avco")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK', 'COMPTABLE')")
    public ResponseEntity<BigDecimal> valorisationAVCO(@PathVariable Long articleId) {
        return ResponseEntity.ok(stockService.calculerValorisationAVCO(articleId));
    }

    // === REAPPROVISIONNEMENT ===

    @GetMapping("/reapprovisionnement")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK')")
    public ResponseEntity<?> articlesASurveiller() {
        return ResponseEntity.ok(stockService.getArticlesASurveiller());
    }

    // === HISTORIQUE ===

    @GetMapping("/historique/{articleId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'OPERATEUR_STOCK')")
    public ResponseEntity<?> historiqueArticle(@PathVariable Long articleId) {
        return ResponseEntity.ok(stockService.getHistoriqueArticle(articleId));
    }
}
