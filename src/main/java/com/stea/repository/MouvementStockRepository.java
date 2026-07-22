package com.stea.repository;

import com.stea.entity.MouvementStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MouvementStockRepository extends JpaRepository<MouvementStock, Long> {

    @Query("SELECT m FROM MouvementStock m WHERE " +
           "(:type IS NULL OR m.type = :type) " +
           "AND (:articleId IS NULL OR m.article.id = :articleId) " +
           "AND (:dateDebut IS NULL OR m.dateMouvement >= :dateDebut) " +
           "AND (:dateFin IS NULL OR m.dateMouvement <= :dateFin)")
    Page<MouvementStock> search(
            @Param("type") MouvementStock.TypeMouvement type,
            @Param("articleId") Long articleId,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin,
            Pageable pageable);

    List<MouvementStock> findTop20ByOrderByDateMouvementDesc();

    List<MouvementStock> findByArticleId(Long articleId);

    @Query("SELECT m.type, COUNT(m) FROM MouvementStock m " +
           "WHERE m.dateMouvement >= :dateDebut GROUP BY m.type")
    List<Object[]> repartitionMouvementsParType(@Param("dateDebut") LocalDateTime dateDebut);

    @Modifying
    @Query("UPDATE MouvementStock m SET m.operateur = NULL WHERE m.operateur.id = :userId")
    void clearUtilisateurReferences(@Param("userId") Long userId);
}
