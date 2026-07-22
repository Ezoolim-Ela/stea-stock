package com.stea.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventaires_physiques")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class InventairePhysique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrepot_id", nullable = false)
    private Entrepot entrepot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeInventaire type = TypeInventaire.COMPTAGE_COMPLET;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutInventaire statut = StatutInventaire.BROUILLON;

    private LocalDate datePrevue;

    private LocalDate dateRealisation;

    private String realisateur;

    @Column(columnDefinition = "TEXT")
    private String observation;

    private Integer nbArticlesComptes = 0;

    private Integer nbEcarts = 0;

    private BigDecimal valeurEcarts = BigDecimal.ZERO;

    private LocalDateTime dateCreation;

    public enum TypeInventaire {
        COMPTAGE_COMPLET, COMPTAGE_CYCLIQUE, COMPTAGE_CATEGORIE
    }

    public enum StatutInventaire {
        BROUILLON, EN_COURS, TERMINE, VALIDE, ANNULE
    }

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
}
