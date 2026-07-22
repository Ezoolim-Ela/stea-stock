package com.stea.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CommandeRequest {

    @Size(max = 150)
    private String client;

    @NotBlank(message = "Le type de flux est requis")
    private String typeFlux;

    private List<LigneCommandeRequest> lignes;
}
