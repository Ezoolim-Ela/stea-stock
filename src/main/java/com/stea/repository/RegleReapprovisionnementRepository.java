package com.stea.repository;

import com.stea.entity.RegleReapprovisionnement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface RegleReapprovisionnementRepository extends JpaRepository<RegleReapprovisionnement, Long> {
    List<RegleReapprovisionnement> findByArticleId(Long articleId);
    List<RegleReapprovisionnement> findByActifTrue();

    @Query("SELECT r FROM RegleReapprovisionnement r JOIN FETCH r.article a WHERE r.actif = true AND a.quantiteStock <= r.seuilMinimum")
    List<RegleReapprovisionnement> findArticlesASurveiller();
}
