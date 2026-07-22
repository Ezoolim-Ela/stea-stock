package com.stea.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "numeross_serie")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class NumeroSerie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    @JsonIgnore
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id")
    private Lot lot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emplacement_id")
    private Emplacement emplacement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutSerie statut = StatutSerie.DISPONIBLE;

    private LocalDate dateAcquisition;

    private LocalDate dateGarantie;

    private String proprietaire;

    @Column(nullable = false)
    private boolean actif = true;

    private LocalDateTime dateCreation;

    public enum StatutSerie {
        DISPONIBLE, EN_SERVICE, HORS_SERVICE, PERDU, VENDU
    }

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
}
