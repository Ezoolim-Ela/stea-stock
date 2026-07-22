package com.stea.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ArticleResponse {
    private Long id;
    private String reference;
    private String designation;
    private String categorie;
    private String type;
    private BigDecimal prixUnitaire;
    private String codeBarre;
    private Integer quantiteStock;
    private Integer seuilAlerte;
    private String description;
    private String photo;
    private Long serviceId;
    private String serviceNom;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
