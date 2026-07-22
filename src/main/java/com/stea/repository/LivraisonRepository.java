package com.stea.repository;

import com.stea.entity.Livraison;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LivraisonRepository extends JpaRepository<Livraison, Long> {

    @Query("SELECT l FROM Livraison l WHERE " +
           "(:search IS NULL OR LOWER(l.numeroBon) LIKE LOWER(CONCAT('%',:search,'%')) " +
           "OR LOWER(l.destinataire) LIKE LOWER(CONCAT('%',:search,'%'))) " +
           "AND (:statut IS NULL OR l.statut = :statut)")
    Page<Livraison> search(
            @Param("search") String search,
            @Param("statut") Livraison.StatutLivraison statut,
            Pageable pageable);

    List<Livraison> findByStatutPaiement(Livraison.StatutPaiementLivraison statutPaiement);

    @Modifying
    @Query("UPDATE Livraison l SET l.commande = NULL WHERE l.commande.id = :commandeId")
    void clearCommandeReferences(@Param("commandeId") Long commandeId);

    @Modifying
    @Query("UPDATE Livraison l SET l.comptableValidateur = NULL WHERE l.comptableValidateur.id = :userId")
    void clearUtilisateurReferences(@Param("userId") Long userId);
}
