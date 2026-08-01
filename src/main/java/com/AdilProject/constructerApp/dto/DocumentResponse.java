package com.AdilProject.constructerApp.dto;

import java.time.LocalDateTime;

public record DocumentResponse(
        String fileName,
        String fileUrl,
        LocalDateTime uploadedAt
) {}
