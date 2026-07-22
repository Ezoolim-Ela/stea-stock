package com.stea.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStats {
    private BigDecimal valeurTotaleStock;
    private Integer quantiteTotaleStock;
    private Long nombreArticles;
    private Long nombreCategories;
    private Long nombreFournisseurs;
    private Long entreesAujourdhui;
    private Long sortiesAujourdhui;
    private Long entreesSemaine;
    private Long sortiesSemaine;
    private Long commandesEnAttente;
    private Long livraisonsEnAttente;
    private Integer alertesRupture;
    private Double tauxLivraisonPonctuelle;
}
