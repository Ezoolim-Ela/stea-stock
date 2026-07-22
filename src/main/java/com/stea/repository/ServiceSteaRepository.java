package com.stea.repository;

import com.stea.entity.ServiceStea;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceSteaRepository extends JpaRepository<ServiceStea, Long> {
    boolean existsByCode(String code);
}
