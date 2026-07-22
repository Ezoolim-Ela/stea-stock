package com.stea.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LigneCommandeResponse {
    private Long id;
    private String articleReference;
    private String articleDesignation;
    private Integer quantite;
    private BigDecimal prixUnitaire;
    private BigDecimal sousTotal;
}
