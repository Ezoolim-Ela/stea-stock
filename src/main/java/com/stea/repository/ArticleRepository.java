package com.stea.repository;

import com.stea.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    boolean existsByReference(String reference);

    Optional<Article> findByReference(String reference);

    @Query("SELECT a FROM Article a WHERE " +
           "(:search IS NULL OR LOWER(a.reference) LIKE LOWER(CONCAT('%',:search,'%')) " +
           "OR LOWER(a.designation) LIKE LOWER(CONCAT('%',:search,'%'))) " +
           "AND (:categorie IS NULL OR a.categorie = :categorie) " +
           "AND (:type IS NULL OR a.type = :type) " +
           "AND (:serviceId IS NULL OR a.service.id = :serviceId) " +
           "AND (:prixMin IS NULL OR a.prixUnitaire >= :prixMin) " +
           "AND (:prixMax IS NULL OR a.prixUnitaire <= :prixMax)")
    Page<Article> search(
            @Param("search") String search,
            @Param("categorie") String categorie,
            @Param("type") String type,
            @Param("serviceId") Long serviceId,
            @Param("prixMin") BigDecimal prixMin,
            @Param("prixMax") BigDecimal prixMax,
            Pageable pageable);

    @Query("SELECT COALESCE(SUM(a.quantiteStock * a.prixUnitaire), 0) FROM Article a")
    BigDecimal calculerValeurTotaleStock();

    @Query("SELECT COUNT(a) FROM Article a WHERE a.quantiteStock <= a.seuilAlerte")
    long countAlertesSeuilCritique();

    @Query("SELECT a FROM Article a WHERE a.quantiteStock <= a.seuilAlerte ORDER BY a.quantiteStock ASC")
    java.util.List<Article> findArticlesEnAlerte();

    @Modifying
    @Query("UPDATE Article a SET a.dernierFournisseur = NULL WHERE a.dernierFournisseur.id = :fournisseurId")
    void clearFournisseurReferences(@Param("fournisseurId") Long fournisseurId);

    @Modifying
    @Query(value = "UPDATE lots SET article_id = NULL WHERE article_id = :articleId", nativeQuery = true)
    void clearLotsReferences(@Param("articleId") Long articleId);

    @Modifying
    @Query(value = "UPDATE lignes_commande SET article_id = NULL WHERE article_id = :articleId", nativeQuery = true)
    void clearLignesCommandeReferences(@Param("articleId") Long articleId);

    @Modifying
    @Query(value = "UPDATE mouvements_stock SET article_id = NULL WHERE article_id = :articleId", nativeQuery = true)
    void clearMouvementsReferences(@Param("articleId") Long articleId);

    @Modifying
    @Query(value = "UPDATE numeross_serie SET article_id = NULL WHERE article_id = :articleId", nativeQuery = true)
    void clearNumerosSerieReferences(@Param("articleId") Long articleId);

    @Modifying
    @Query(value = "UPDATE regles_reapprovisionnement SET article_id = NULL WHERE article_id = :articleId", nativeQuery = true)
    void clearReglesReferences(@Param("articleId") Long articleId);

    @Modifying
    @Query(value = "UPDATE lignes_inventaire SET article_id = NULL WHERE article_id = :articleId", nativeQuery = true)
    void clearLignesInventaireReferences(@Param("articleId") Long articleId);
}
