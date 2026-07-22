package com.stea.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class FournisseurRequest {

    @NotBlank(message = "Le code est requis")
    @Size(max = 30)
    private String code;

    @NotBlank(message = "La désignation est requise")
    @Size(max = 200)
    private String designation;

    @Size(max = 100)
    private String categorieProduits;

    @Size(max = 150)
    private String contactPrincipal;

    @Size(max = 30)
    private String telephone;

    @Email(message = "Format d'email invalide")
    private String email;

    @Size(max = 100)
    private String pays;

    @Size(max = 100)
    private String ville;

    private String statut;
}
