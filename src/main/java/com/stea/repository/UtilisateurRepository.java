package com.stea.repository;

import com.stea.entity.Utilisateur;
import com.stea.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Utilisateur> findByRoleLibelleAndStatut(Role.RoleEnum roleLibelle, Utilisateur.StatutEnum statut);

    @Query("SELECT u FROM Utilisateur u WHERE " +
           "(:search IS NULL OR LOWER(u.identite) LIKE LOWER(CONCAT('%',:search,'%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%',:search,'%'))) " +
           "AND (:statut IS NULL OR u.statut = :statut) " +
           "AND (:role IS NULL OR u.role.libelle = :role)")
    Page<Utilisateur> search(
            @Param("search") String search,
            @Param("statut") Utilisateur.StatutEnum statut,
            @Param("role") String role,
            Pageable pageable);
}
