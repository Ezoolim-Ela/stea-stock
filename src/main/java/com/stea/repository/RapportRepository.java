package com.stea.repository;

import com.stea.entity.Rapport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RapportRepository extends JpaRepository<Rapport, Long> {
    List<Rapport> findByType(Rapport.TypeRapport type);
}
