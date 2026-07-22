package com.stea.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "mouvements_stock")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MouvementStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TypeMouvement type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    @Column(name = "article_reference_text", length = 100)
    private String articleReferenceText;

    @Column(name = "article_designation_text", length = 255)
    private String articleDesignationText;

    @Column(nullable = false)
    private Integer quantite;

    @Column(length = 10)
    @Builder.Default
    private String unite = "Un";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emplacement_source")
    private Emplacement emplacementSource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emplacement_destination")
    private Emplacement emplacementDestination;

    @Column(name = "emplacement_source_text", length = 255)
    private String emplacementSourceText;

    @Column(name = "emplacement_destination_text", length = 255)
    private String emplacementDestinationText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operateur_id")
    private Utilisateur operateur;

    @Column(length = 50)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrepot_source")
    private Entrepot entrepotSource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrepot_destination")
    private Entrepot entrepotDestination;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id")
    private Lot lot;

    @Column(name = "numero_serie", length = 50)
    private String numeroSerie;

    @Column(length = 500)
    private String observation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventaire_id")
    private InventairePhysique inventaire;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private StatutMouvement statut = StatutMouvement.VALIDE;

    @CreationTimestamp
    @Column(name = "date_mouvement", updatable = false)
    private LocalDateTime dateMouvement;

    public enum TypeMouvement {
        ENTREE, SORTIE, TRANSFERT, AJUSTEMENT, INVENTAIRE
    }

    public enum StatutMouvement {
        EN_ATTENTE, VALIDE, ANNULE, TRAITE
    }
}
