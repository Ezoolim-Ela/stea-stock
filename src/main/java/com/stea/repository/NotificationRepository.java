package com.stea.repository;

import com.stea.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByDestinataireIdOrderByCreatedAtDesc(Long destinataireId, Pageable pageable);

    long countByDestinataireIdAndStatutLecture(Long destinataireId, Notification.StatutLecture statut);

    @Query("SELECT n FROM Notification n WHERE n.destinataire.id = :userId AND n.statutLecture = 'NON_LUE' ORDER BY n.createdAt DESC")
    Page<Notification> findNonLues(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.destinataire.id = :destinataireId AND n.statutLecture = 'NON_LUE' ORDER BY n.createdAt DESC")
    List<Notification> findByDestinataireIdAndLuFalse(@Param("destinataireId") Long destinataireId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.destinataire.id = :destinataireId AND n.statutLecture = 'NON_LUE'")
    long countByDestinataireIdAndLuFalse(@Param("destinataireId") Long destinataireId);

    @Modifying
    @Query("UPDATE Notification n SET n.destinataire = NULL WHERE n.destinataire.id = :userId")
    void clearDestinataireReferences(@Param("userId") Long userId);
}
