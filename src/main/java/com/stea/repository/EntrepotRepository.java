package com.stea.repository;

import com.stea.entity.Entrepot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EntrepotRepository extends JpaRepository<Entrepot, Long> {
    Optional<Entrepot> findByCode(String code);
    List<Entrepot> findByActifTrue();
    boolean existsByCode(String code);
}
