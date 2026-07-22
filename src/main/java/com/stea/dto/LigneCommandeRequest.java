package com.stea.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LigneCommandeRequest {

    @NotNull(message = "L'article est requis")
    private Long articleId;

    @NotNull(message = "La quantité est requise")
    @Positive(message = "La quantité doit être strictement positive")
    private Integer quantite;

    @NotNull(message = "Le prix unitaire est requis")
    @DecimalMin(value = "0.01", message = "Le prix doit être strictement positif")
    private BigDecimal prixUnitaire;
}
