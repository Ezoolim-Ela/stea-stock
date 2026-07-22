package com.stea.repository;

import com.stea.entity.ConfigurationSysteme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfigurationSystemeRepository extends JpaRepository<ConfigurationSysteme, Long> {
    Optional<ConfigurationSysteme> findFirstBy();
}
