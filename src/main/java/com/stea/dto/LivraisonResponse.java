package com.stea.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LivraisonResponse {
    private Long id;
    private String numeroBon;
    private LocalDate datePrevue;
    private LocalDate dateReceptionEffective;
    private String destinataire;
    private String contenu;
    private String vehicule;
    private Integer nombreColis;
    private String statut;
    private Boolean bordereauTransmis;
    private Boolean factureEnvoyee;
    private BigDecimal montantRestant;
    private Long commandeId;
    private String commandeNumero;
    private String observation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
