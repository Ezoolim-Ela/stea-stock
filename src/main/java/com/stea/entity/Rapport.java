package com.stea.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rapports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rapport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeRapport type;

    private String periode;

    @Column(nullable = false)
    private LocalDateTime dateGeneration;

    @Column(columnDefinition = "TEXT")
    private String contenu;

    public enum TypeRapport {
        EVOLUTION_COMMANDES, PERFORMANCE_FOURNISSEURS, INDICATEURS_STOCK
    }
}
