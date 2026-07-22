package com.stea.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommandeResponse {
    private Long id;
    private String numero;
    private String client;
    private String typeFlux;
    private BigDecimal total;
    private String statut;
    private String statutPaiement;
    private BigDecimal pourcentagePaiement;
    private BigDecimal montantPaye;
    private LocalDateTime datePaiement;
    private LocalDate delaiLivraison;
    private String fournisseurNom;
    private String utilisateur;
    private List<LigneCommandeResponse> lignes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
