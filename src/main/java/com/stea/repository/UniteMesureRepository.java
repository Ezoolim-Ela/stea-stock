package com.stea.repository;

import com.stea.entity.UniteMesure;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UniteMesureRepository extends JpaRepository<UniteMesure, Long> {
    Optional<UniteMesure> findByCode(String code);
    List<UniteMesure> findByCategorie(UniteMesure.CategorieUnite categorie);
    boolean existsByCode(String code);
}
