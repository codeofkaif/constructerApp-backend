package com.AdilProject.constructerApp.service;

import com.AdilProject.constructerApp.dto.AdminNotificationRequest;
import com.AdilProject.constructerApp.entity.Notification;
import com.AdilProject.constructerApp.entity.User;
import com.AdilProject.constructerApp.entity.type.Role;
import com.AdilProject.constructerApp.exception.ResourceNotFoundException;
import com.AdilProject.constructerApp.repository.NotificationRepository;
import com.AdilProject.constructerApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminNotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    // saveAll() — ek hi batch mein sabhi notifications insert karta hai.
    // 50 clients ke liye 50 alag save() calls se behtar hai (batch insert).
    // application.properties mein hibernate.jdbc.batch_size=20 set kiya hai.
    @Transactional
    public void send(AdminNotificationRequest req) {
        List<User> recipients = req.broadcastToAll()
                ? userRepository.findByRole(Role.CLIENT)
                : List.of(userRepository.findById(req.clientId())
                        .orElseThrow(() -> new ResourceNotFoundException("Client not found")));

        List<Notification> notifications = recipients.stream()
                .map(u -> Notification.builder()
                        .message(req.message())
                        .user(u)
                        .isRead(false)
                        .build())
                .toList();

        notificationRepository.saveAll(notifications);
    }
}
