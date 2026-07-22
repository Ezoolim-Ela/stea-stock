package com.stea.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "unites_mesure")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UniteMesure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String symbole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategorieUnite categorie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unite_parente_id")
    private UniteMesure uniteParente;

    @Column(nullable = false)
    private Double facteurConversion = 1.0;

    public enum CategorieUnite {
        UNITE, POIDS, VOLUME, LONGUEUR, SURFACE, TEMPS
    }
}
