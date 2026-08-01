package com.AdilProject.constructerApp.dto;

import java.time.LocalDateTime;

public record UpdateResponse(
        String title,
        String description,
        String thumbnailUrl,
        LocalDateTime createdAt
) {}
