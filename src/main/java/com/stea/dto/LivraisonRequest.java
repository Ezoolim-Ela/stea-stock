package com.stea.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LivraisonRequest {

    @NotBlank(message = "Le numero de bon est requis")
    @Size(max = 30)
    private String numeroBon;

    private LocalDate datePrevue;

    @Size(max = 150)
    private String destinataire;

    @Size(max = 255)
    private String contenu;

    @Size(max = 100)
    private String vehicule;

    private Integer nombreColis;

    private String statut;

    private Long commandeId;

    private String observation;
}
