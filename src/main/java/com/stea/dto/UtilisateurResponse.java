package com.stea.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UtilisateurResponse {
    private Long id;
    private String photo;
    private String identite;
    private String email;
    private String role;
    private String statut;
    private LocalDateTime dateDerniereConnexion;
    private LocalDateTime createdAt;
}
