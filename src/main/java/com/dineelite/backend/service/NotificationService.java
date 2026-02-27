package com.dineelite.backend.service;

import com.dineelite.backend.entity.Notification;
import com.dineelite.backend.entity.User;
import com.dineelite.backend.enums.NotificationType;
import com.dineelite.backend.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void createNotification(User recipient, User sender, NotificationType type, String message) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setSender(sender);
        notification.setType(type);
        notification.setMessage(message);
        notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsForUser(Integer userId) {
        return notificationRepository.findByRecipient_UserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getNotificationsForUserByEmail(String email) {
        return notificationRepository.findByRecipient_EmailOrderByCreatedAtDesc(email);
    }

    public void markAsRead(Integer notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    public Long getUnreadCount(Integer userId) {
        return notificationRepository.countByRecipient_UserIdAndIsReadFalse(userId);
    }

    public Long getUnreadCountByEmail(String email) {
        return notificationRepository.countByRecipient_EmailAndIsReadFalse(email);
    }
}
