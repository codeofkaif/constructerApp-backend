package com.AdilProject.constructerApp.dto;

public record AdminOverviewResponse(
        long totalClients,
        long activeProjects,
        double totalRevenueCollected,
        double pendingPayments
) {}
