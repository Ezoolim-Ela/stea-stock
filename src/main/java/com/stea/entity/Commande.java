package com.stea.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commandes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String numero;

    @Column(length = 150)
    private String client;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_flux", nullable = false, length = 20)
    private TypeFlux typeFlux;

    @Column(nullable = false, precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatutCommande statut = StatutCommande.BROUILLON;

    @Column(name = "pourcentage_paiement", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal pourcentagePaiement = BigDecimal.ZERO;

    @Column(name = "montant_paye", precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal montantPaye = BigDecimal.ZERO;

    @Column(name = "date_paiement")
    private LocalDateTime datePaiement;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_paiement", length = 30)
    @Builder.Default
    private StatutPaiement statutPaiement = StatutPaiement.NON_PAYE;

    @Column(name = "delai_livraison")
    private LocalDate delaiLivraison;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fournisseur_id")
    private Fournisseur fournisseur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comptable_validateur_id")
    private Utilisateur comptableValidateur;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LigneCommande> lignes = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    @Column(length = 500)
    private String observation;

    public enum TypeFlux {
        ACHAT, VENTE
    }

    public enum StatutCommande {
        BROUILLON, CONFIRMEE, VALIDEE, EN_COURS_LIVRAISON, LIVREE, ANNULEE
    }

    public enum StatutPaiement {
        NON_PAYE, PARTIEL, PAYE
    }
}
