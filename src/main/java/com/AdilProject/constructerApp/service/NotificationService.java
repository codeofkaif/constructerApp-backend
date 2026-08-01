package com.AdilProject.constructerApp.service;

import com.AdilProject.constructerApp.dto.NotificationResponse;
import com.AdilProject.constructerApp.entity.Notification;
import com.AdilProject.constructerApp.entity.User;
import com.AdilProject.constructerApp.exception.ResourceNotFoundException;
import com.AdilProject.constructerApp.repository.NotificationRepository;
import com.AdilProject.constructerApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public List<NotificationResponse> getAll(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(n -> new NotificationResponse(n.getId(), n.getMessage(), n.isRead(), n.getCreatedAt()))
                .toList();
    }

    // @Transactional + dirty checking: save() call ki zaroorat nahi —
    // entity managed hai, method end pe automatically flush ho jayega
    @Transactional
    public void markRead(String email, Long id) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        // Ownership check — koi doosre ka notification read mark na kar sake
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("This notification doesn't belong to you");
        }
        notification.setRead(true);
    }

    @Transactional
    public void markAllRead(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        notifications.forEach(n -> n.setRead(true));
    }
}
