package com.stea.repository;

import com.stea.entity.Fournisseur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {

    boolean existsByCode(String code);

    @Query("SELECT f FROM Fournisseur f WHERE " +
           "(:search IS NULL OR LOWER(f.designation) LIKE LOWER(CONCAT('%',:search,'%')) " +
           "OR LOWER(f.code) LIKE LOWER(CONCAT('%',:search,'%'))) " +
           "AND (:statut IS NULL OR f.statut = :statut)")
    Page<Fournisseur> search(
            @Param("search") String search,
            @Param("statut") Fournisseur.StatutFournisseur statut,
            Pageable pageable);

    long countByStatut(Fournisseur.StatutFournisseur statut);
}
