package com.stea.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lignes_inventaire")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class LigneInventaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventaire_id", nullable = false)
    private InventairePhysique inventaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emplacement_id")
    private Emplacement emplacement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id")
    private Lot lot;

    @Column(nullable = false)
    private Integer quantiteTheorique;

    @Column(nullable = false)
    private Integer quantitePhysique;

    @Column(nullable = false)
    private Integer ecart = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutLigne statut = StatutLigne.EN_ATTENTE;

    private String observation;

    public enum StatutLigne {
        EN_ATTENTE, COMPTE, VALIDE, ECART
    }
}
