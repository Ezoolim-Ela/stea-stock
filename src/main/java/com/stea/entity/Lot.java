package com.stea.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lots")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Lot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroLot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    @JsonIgnore
    private Article article;

    @Column(nullable = false)
    private Integer quantiteInitiale;

    @Column(nullable = false)
    private Integer quantiteRestante;

    private LocalDate dateProduction;

    private LocalDate dateExpiration;

    private BigDecimal coutUnitaire;

    private String fournisseur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutLot statut = StatutLot.ACTIF;

    private LocalDateTime dateCreation;

    public enum StatutLot {
        ACTIF, EXPIRE, BLOQUE, CONSOMME
    }

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
}
