package com.stea.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageRequest {
    @NotNull(message = "Le destinataire est requis")
    private Long destinataireId;

    @NotBlank(message = "Le message est requis")
    private String contenu;

    private String sujet;
}
