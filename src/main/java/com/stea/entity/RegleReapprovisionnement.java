package com.stea.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "regles_reapprovisionnement")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RegleReapprovisionnement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrepot_id", nullable = false)
    private Entrepot entrepot;

    @Column(nullable = false)
    private Integer seuilMinimum;

    @Column(nullable = false)
    private Integer quantiteObjectif;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StrategieReappro strategie = StrategieReappro.COMMANDE_FOURNISSEUR;

    @Column(nullable = false)
    private Integer delaiReapproJours = 7;

    private boolean actif = true;

    private LocalDateTime dateCreation;

    public enum StrategieReappro {
        COMMANDE_FOURNISSEUR, TRANSFERT_AUTRE_ENTREPOT, PRODUCTION_INTERNE
    }

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
}
