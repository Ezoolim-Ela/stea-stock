package com.stea.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FournisseurResponse {
    private Long id;
    private String code;
    private String designation;
    private String categorieProduits;
    private String contactPrincipal;
    private String telephone;
    private String email;
    private String pays;
    private String ville;
    private LocalDateTime derniereCommande;
    private String statut;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
