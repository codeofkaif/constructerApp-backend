package com.AdilProject.constructerApp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminPostUpdateRequest(
        @NotNull Long projectId,
        @NotBlank String title,
        String description,
        String thumbnailUrl
) {}
