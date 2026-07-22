package com.stea.repository;

import com.stea.entity.Commande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommandeRepository extends JpaRepository<Commande, Long> {

    Optional<Commande> findByNumero(String numero);

    boolean existsByNumero(String numero);

    @Query("SELECT c FROM Commande c WHERE " +
           "(:search IS NULL OR LOWER(c.numero) LIKE LOWER(CONCAT('%',:search,'%')) " +
           "OR LOWER(c.client) LIKE LOWER(CONCAT('%',:search,'%'))) " +
           "AND (:typeFlux IS NULL OR c.typeFlux = :typeFlux) " +
           "AND (:statut IS NULL OR c.statut = :statut)")
    Page<Commande> search(
            @Param("search") String search,
            @Param("typeFlux") Commande.TypeFlux typeFlux,
            @Param("statut") Commande.StatutCommande statut,
            Pageable pageable);

    @Query("SELECT c.statut, COUNT(c) FROM Commande c GROUP BY c.statut")
    java.util.List<Object[]> repartitionStatuts();

    @Query("SELECT COUNT(c) FROM Commande c WHERE c.statut = com.stea.entity.Commande$StatutCommande.LIVREE " +
           "AND c.createdAt >= :dateDebut")
    long countLivreesDepuis(@Param("dateDebut") java.time.LocalDateTime dateDebut);

    @Query("SELECT COUNT(c) FROM Commande c WHERE c.statut = com.stea.entity.Commande$StatutCommande.CONFIRMEE " +
           "AND c.createdAt >= :dateDebut")
    long countConfirmeesDepuis(@Param("dateDebut") java.time.LocalDateTime dateDebut);

    long countByStatut(Commande.StatutCommande statut);

    List<Commande> findByStatut(Commande.StatutCommande statut);

    List<Commande> findByStatutPaiement(Commande.StatutPaiement statutPaiement);

    @Query("SELECT c.statutPaiement, COUNT(c) FROM Commande c GROUP BY c.statutPaiement")
    List<Object[]> compterParStatutPaiement();

    @Modifying
    @Query("UPDATE Commande c SET c.fournisseur = NULL WHERE c.fournisseur.id = :fournisseurId")
    void clearFournisseurReferences(@Param("fournisseurId") Long fournisseurId);

    @Modifying
    @Query("UPDATE Commande c SET c.utilisateur = NULL WHERE c.utilisateur.id = :userId")
    void clearUtilisateurReferences(@Param("userId") Long userId);
}
