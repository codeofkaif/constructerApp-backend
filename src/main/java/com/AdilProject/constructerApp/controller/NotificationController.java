package com.AdilProject.constructerApp.controller;

import com.AdilProject.constructerApp.dto.NotificationResponse;
import com.AdilProject.constructerApp.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> list(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(notificationService.getAll(user.getUsername()));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markRead(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id) {
        notificationService.markRead(user.getUsername(), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllRead(@AuthenticationPrincipal UserDetails user) {
        notificationService.markAllRead(user.getUsername());
        return ResponseEntity.noContent().build();
    }
}
