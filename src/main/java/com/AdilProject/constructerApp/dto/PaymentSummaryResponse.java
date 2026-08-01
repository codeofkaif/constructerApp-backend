package com.AdilProject.constructerApp.dto;

public record PaymentSummaryResponse(
        Double totalBudget,
        Double paidAmount,
        Double remainingAmount
) {}
