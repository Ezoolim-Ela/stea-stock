package com.stea.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageResponse {
    private Long id;
    private Long expediteurId;
    private String expediteurNom;
    private Long destinataireId;
    private String destinataireNom;
    private String contenu;
    private String sujet;
    private boolean lu;
    private LocalDateTime date;
}
