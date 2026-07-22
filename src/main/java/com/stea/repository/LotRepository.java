package com.stea.repository;

import com.stea.entity.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LotRepository extends JpaRepository<Lot, Long> {
    Optional<Lot> findByNumeroLot(String numeroLot);
    List<Lot> findByArticleId(Long articleId);
    List<Lot> findByArticleIdAndStatut(Long articleId, Lot.StatutLot statut);

    @Query("SELECT l FROM Lot l WHERE l.article.id = :articleId AND l.statut = 'ACTIF' AND l.quantiteRestante > 0 ORDER BY l.dateExpiration ASC NULLS LAST")
    List<Lot> findLotsDisponiblesFIFO(Long articleId);

    @Query("SELECT l FROM Lot l WHERE l.dateExpiration <= :date AND l.statut = 'ACTIF'")
    List<Lot> findLotsExpires(LocalDate date);

    @Query("SELECT l FROM Lot l WHERE l.dateExpiration BETWEEN :dateDebut AND :dateFin AND l.statut = 'ACTIF'")
    List<Lot> findLotsExpirationProche(LocalDate dateDebut, LocalDate dateFin);

    long countByStatut(Lot.StatutLot statut);
}
