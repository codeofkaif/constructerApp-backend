package com.AdilProject.constructerApp.dto;

import java.time.LocalDateTime;

// 'id' field zaroori hai — frontend PATCH /notifications/{id}/read mein yahi use karta hai
public record NotificationResponse(
        Long id,
        String message,
        boolean isRead,
        LocalDateTime createdAt
) {}
