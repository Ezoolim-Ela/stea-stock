package com.stea.repository;

import com.stea.entity.NumeroSerie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface NumeroSerieRepository extends JpaRepository<NumeroSerie, Long> {
    Optional<NumeroSerie> findByNumero(String numero);
    List<NumeroSerie> findByArticleId(Long articleId);
    List<NumeroSerie> findByArticleIdAndStatut(Long articleId, NumeroSerie.StatutSerie statut);
    List<NumeroSerie> findByLotId(Long lotId);
    long countByArticleIdAndStatut(Long articleId, NumeroSerie.StatutSerie statut);
}
