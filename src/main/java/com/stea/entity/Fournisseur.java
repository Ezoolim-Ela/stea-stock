package com.stea.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "fournisseurs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Fournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false, length = 200)
    private String designation;

    @Column(name = "categorie_produits", length = 100)
    private String categorieProduits;

    @Column(name = "contact_principal", length = 150)
    private String contactPrincipal;

    @Column(length = 30)
    private String telephone;

    @Column(length = 150)
    private String email;

    @Column(name = "equipements_fournis", length = 255)
    private String equipementsFournis;

    @Column(name = "pays_origine", length = 100)
    private String paysOrigine;

    @Column(length = 100)
    private String pays;

    @Column(length = 100)
    private String ville;

    @Column(name = "derniere_commande")
    private LocalDateTime derniereCommande;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private StatutFournisseur statut = StatutFournisseur.ACTIF;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum StatutFournisseur {
        ACTIF, INACTIF
    }
}
