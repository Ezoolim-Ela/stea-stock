package com.stea.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MouvementStockRequest {

    @NotBlank(message = "Le type de mouvement est requis")
    private String type;

    private Long articleId;

    private String articleReference;

    private String articleDesignation;

    @NotNull(message = "La quantité est requise")
    @Positive(message = "La quantité doit être strictement positive")
    private Integer quantite;

    private String unite;

    private String emplacementSource;

    private String emplacementDestination;

    private String reference;
}
