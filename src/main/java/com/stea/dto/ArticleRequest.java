package com.stea.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ArticleRequest {

    @NotBlank(message = "La référence est requise")
    @Size(max = 30)
    private String reference;

    @NotBlank(message = "La désignation est requise")
    @Size(max = 255)
    private String designation;

    @Size(max = 100)
    private String categorie;

    private String type;

    @NotNull(message = "Le prix unitaire est requis")
    @DecimalMin(value = "0.01", message = "Le prix doit être strictement positif")
    private BigDecimal prixUnitaire;

    @Size(max = 50)
    private String codeBarre;

    @Min(value = 0, message = "La quantité ne peut pas être négative")
    private Integer quantiteStock;

    private Integer seuilAlerte;

    @Size(max = 255)
    private String description;

    private String photo;

    private Long serviceId;
}
