package com.stea.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinataire_id", nullable = false)
    private Utilisateur destinataire;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Canal canal;

    @Column(nullable = false, length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_lecture", length = 20)
    @Builder.Default
    private StatutLecture statutLecture = StatutLecture.NON_LUE;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    @Builder.Default
    private TypeNotification type = TypeNotification.SYSTEME;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum Canal {
        EMAIL, SMS, IN_APP
    }

    public enum StatutLecture {
        LUE, NON_LUE
    }

    public enum TypeNotification {
        STOCK_FAIBLE, COMMANDE, LIVRAISON, SYSTEME, ALERTE, PAIEMENT, UTILISATEUR
    }
}
