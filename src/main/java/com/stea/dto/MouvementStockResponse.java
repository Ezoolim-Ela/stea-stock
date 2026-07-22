package com.stea.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MouvementStockResponse {
    private Long id;
    private String type;
    private String articleReference;
    private String articleDesignation;
    private Integer quantite;
    private String unite;
    private String emplacementSource;
    private String emplacementDestination;
    private String operateur;
    private String reference;
    private String statut;
    private LocalDateTime dateMouvement;
}
