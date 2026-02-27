package com.dineelite.backend.repository;

import com.dineelite.backend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByRecipient_UserIdOrderByCreatedAtDesc(Integer userId);
    List<Notification> findByRecipient_EmailOrderByCreatedAtDesc(String email);
    Long countByRecipient_UserIdAndIsReadFalse(Integer userId);
    Long countByRecipient_EmailAndIsReadFalse(String email);
    void deleteByRecipient(com.dineelite.backend.entity.User recipient);
    void deleteBySender(com.dineelite.backend.entity.User sender);
}
