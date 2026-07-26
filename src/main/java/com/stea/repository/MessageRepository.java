package com.stea.repository;

import com.stea.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByDestinataireIdOrderByCreatedAtDesc(Long destinataireId, Pageable pageable);

    Page<Message> findByExpediteurIdOrderByCreatedAtDesc(Long expediteurId, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE (m.expediteur.id = :userId AND m.destinataire.id = :otherId) OR (m.expediteur.id = :otherId AND m.destinataire.id = :userId) ORDER BY m.createdAt ASC")
    List<Message> findConversation(@Param("userId") Long userId, @Param("otherId") Long otherId);

    long countByDestinataireIdAndLuFalse(Long destinataireId);
}
