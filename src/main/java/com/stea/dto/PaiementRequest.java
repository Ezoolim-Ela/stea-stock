package com.stea.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaiementRequest {
    private BigDecimal pourcentagePaiement;
    private LocalDate delaiLivraison;
    private String observation;
}
