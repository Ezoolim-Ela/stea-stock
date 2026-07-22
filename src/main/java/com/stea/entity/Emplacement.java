package com.stea.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "emplacements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Emplacement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeEmplacement type = TypeEmplacement.INTERNE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrepot_id", nullable = false)
    @JsonIgnore
    private Entrepot entrepot;

    @Column(nullable = false)
    private Integer capaciteMax;

    @Column(nullable = false)
    private Integer capaciteActuelle = 0;

    private String zone;

    private String rayon;

    private String niveau;

    @Column(nullable = false)
    private boolean actif = true;

    private LocalDateTime dateCreation;

    public enum TypeEmplacement {
        VENDEUR, VUE, INTERNE, CLIENT, TRANSPORT, PRODUCTION, REBUT
    }

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
}
