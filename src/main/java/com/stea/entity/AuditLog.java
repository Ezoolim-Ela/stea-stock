package com.stea.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Utilisateur utilisateur;

    @Column(length = 50)
    private String email;

    @Column(length = 20)
    private String role;

    @Column(nullable = false, length = 50)
    private String action;

    @Column(nullable = false, length = 50)
    private String entite;

    @Column(name = "entite_id")
    private Long entiteId;

    @Column(length = 2000)
    private String details;

    @Column(length = 100)
    private String endpoint;

    @Column(length = 10)
    private String methode;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(nullable = false)
    @Builder.Default
    private Boolean succes = true;

    @Column(length = 500)
    private String erreur;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}