package com.dineelite.backend.controller;

import com.dineelite.backend.entity.Notification;
import com.dineelite.backend.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(org.springframework.security.core.Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(notificationService.getNotificationsForUserByEmail(email));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(org.springframework.security.core.Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(notificationService.getUnreadCountByEmail(email));
    }

    @PostMapping("/read/{id}")
    public ResponseEntity<?> markAsRead(@PathVariable Integer id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
