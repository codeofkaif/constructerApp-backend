package com.AdilProject.constructerApp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank
    String name,
    @Email
    @NotBlank
    String email,
    @Size(min = 8  , message= "Password must be at least 8 characters")
    @Pattern(regexp = ".*\\d.*",message = "Password must contain a number")
    String password,
    String phone
)
{}
