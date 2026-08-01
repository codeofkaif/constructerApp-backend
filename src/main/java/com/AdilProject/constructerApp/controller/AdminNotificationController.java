package com.AdilProject.constructerApp.controller;

import com.AdilProject.constructerApp.dto.AdminNotificationRequest;
import com.AdilProject.constructerApp.service.AdminNotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/notifications")
@RequiredArgsConstructor
public class AdminNotificationController {

    private final AdminNotificationService adminNotificationService;

    @PostMapping
    public ResponseEntity<Void> send(@Valid @RequestBody AdminNotificationRequest req) {
        adminNotificationService.send(req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
