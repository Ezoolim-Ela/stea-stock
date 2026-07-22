package com.stea.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurRequest {
    @NotBlank(message = "L'identite est obligatoire")
    private String identite;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    private String motDePasse;

    @NotNull(message = "Le role est obligatoire")
    private String role;

    private String photo;

    private String statut;
}
