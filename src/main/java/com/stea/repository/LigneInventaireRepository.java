package com.stea.repository;

import com.stea.entity.LigneInventaire;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LigneInventaireRepository extends JpaRepository<LigneInventaire, Long> {
    List<LigneInventaire> findByInventaireId(Long inventaireId);
    List<LigneInventaire> findByInventaireIdAndEcartNot(Long inventaireId, Integer ecart);
    long countByInventaireId(Long inventaireId);
    long countByInventaireIdAndEcartNot(Long inventaireId, Integer ecart);
}
