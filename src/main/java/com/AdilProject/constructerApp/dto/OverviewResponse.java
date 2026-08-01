package com.AdilProject.constructerApp.dto;

import java.time.LocalDate;

public record OverviewResponse(
        Integer overallProgress,
        String currentStage,
        LocalDate stageStartDate, LocalDate stageEstCompletion, String nextMilestoneName,
        LocalDate nextMilestoneDate
) {}
