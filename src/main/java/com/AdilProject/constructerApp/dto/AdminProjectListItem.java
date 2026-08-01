package com.AdilProject.constructerApp.dto;

// JPQL constructor expression se seedha populate hoga — field order exactly match hona chahiye
// ProjectRepository ki findAllForAdminList() query mein jaise pass ho raha hai
public record AdminProjectListItem(
        Long projectId,
        String clientName,
        String title,
        String location,
        Integer overallProgress,
        String currentStage,
        Double totalBudget,
        Double paidAmount
) {}
