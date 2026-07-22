package com.stea.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DashboardResponse {
    private BigDecimal valeurStock;
    private long alertesSeuilCritique;
    private double tauxLivraisonPonctuelle;
    private long commandesEnRetard;
    private long fournisseursActifs;
    private List<Object[]> repartitionMouvements;
    private List<Object[]> repartitionStatutsCommandes;
    private List<MouvementStockResponse> derniersMouvements;
    private List<ArticleResponse> articlesEnAlerte;
}
