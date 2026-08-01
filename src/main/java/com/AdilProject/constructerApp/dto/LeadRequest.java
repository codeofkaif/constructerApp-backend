package com.AdilProject.constructerApp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LeadRequest(
        @NotBlank String name,
        @NotBlank
        @Pattern(
                regexp = "^(\\+91)?[6-9]\\d{9}$",
                message = "Enter a valid Indian phone number"
        )
        String phone,
        String message
) {}
