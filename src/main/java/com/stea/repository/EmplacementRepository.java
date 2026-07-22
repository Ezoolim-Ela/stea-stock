package com.stea.repository;

import com.stea.entity.Emplacement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface EmplacementRepository extends JpaRepository<Emplacement, Long> {
    List<Emplacement> findByEntrepotId(Long entrepotId);
    List<Emplacement> findByEntrepotIdAndActifTrue(Long entrepotId);
    Optional<Emplacement> findByCodeAndEntrepotId(String code, Long entrepotId);
    boolean existsByCodeAndEntrepotId(String code, Long entrepotId);

    @Query("SELECT e FROM Emplacement e WHERE e.entrepot.id = :entrepotId AND e.capaciteActuelle < e.capaciteMax")
    List<Emplacement> findDisponibles(Long entrepotId);
}
