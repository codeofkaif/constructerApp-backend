package com.AdilProject.constructerApp.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminNotificationRequest(
        Long clientId,          // null hoga agar broadcastToAll = true
        boolean broadcastToAll,
        @NotBlank String message
) {}
