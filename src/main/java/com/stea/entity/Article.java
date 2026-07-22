package com.stea.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "articles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30)
    private String reference;

    @Column(nullable = false, length = 255)
    private String designation;

    @Column(length = 100)
    private String categorie;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TypeArticle type;

    @Column(name = "prix_unitaire", nullable = false, precision = 12, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(name = "code_barre", length = 50)
    private String codeBarre;

    @Column(name = "quantite_stock", nullable = false)
    @Builder.Default
    private Integer quantiteStock = 0;

    @Column(name = "seuil_alerte")
    @Builder.Default
    private Integer seuilAlerte = 10;

    @Column(length = 255)
    private String description;

    @Column(length = 255)
    private String photo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dernier_fournisseur_id")
    private Fournisseur dernierFournisseur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private ServiceStea service;

    @Column(name = "cout_moyen_pondere", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal coutMoyenPondere = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum TypeArticle {
        MATERIEL, CONSOMMABLE, SERVICE
    }
}
