package com.stea.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "livraisons")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Livraison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_bon", nullable = false, unique = true, length = 30)
    private String numeroBon;

    @Column(name = "date_prevue")
    private LocalDate datePrevue;

    @Column(name = "date_reception_effective")
    private LocalDate dateReceptionEffective;

    @Column(length = 150)
    private String destinataire;

    @Column(length = 255)
    private String contenu;

    @Column(length = 100)
    private String vehicule;

    @Column(name = "nombre_colis")
    @Builder.Default
    private Integer nombreColis = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatutLivraison statut = StatutLivraison.EN_ATTENTE;

    @Column(name = "bordereau_transmis")
    @Builder.Default
    private Boolean bordereauTransmis = false;

    @Column(name = "facture_envoyee")
    @Builder.Default
    private Boolean factureEnvoyee = false;

    @Column(name = "montant_restant", precision = 14, scale = 2)
    private BigDecimal montantRestant;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_paiement", length = 30)
    private StatutPaiementLivraison statutPaiement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id")
    private Commande commande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comptable_validateur_id")
    private Utilisateur comptableValidateur;

    @Column(name = "date_validation_paiement")
    private LocalDateTime dateValidationPaiement;

    @Column(length = 500)
    private String observation;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum StatutLivraison {
        EN_ATTENTE, EN_COURS, LIVREE, BORDEREAU_TRANSMIS, FACTURE_ENVOYEE, RETOURNE, ANNULEE
    }

    public enum StatutPaiementLivraison {
        EN_ATTENTE_PAIEMENT, PAIEMENT_VALIDE, PAIEMENT_REFUSE
    }
}
