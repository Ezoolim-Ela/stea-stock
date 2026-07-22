package com.stea.repository;

import com.stea.entity.InventairePhysique;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface InventairePhysiqueRepository extends JpaRepository<InventairePhysique, Long> {
    Optional<InventairePhysique> findByReference(String reference);
    List<InventairePhysique> findByEntrepotId(Long entrepotId);
    List<InventairePhysique> findByStatut(InventairePhysique.StatutInventaire statut);
    List<InventairePhysique> findByStatutIn(List<InventairePhysique.StatutInventaire> statuts);
}
