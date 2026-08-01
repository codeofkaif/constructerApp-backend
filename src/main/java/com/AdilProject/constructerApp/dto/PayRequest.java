package com.AdilProject.constructerApp.dto;

import com.AdilProject.constructerApp.entity.type.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PayRequest(
        @Positive Double amount,
        @NotNull PaymentMethod method
) {}
