package com.AdilProject.constructerApp.dto;

import java.time.LocalDate;
import java.util.List;

// Partial update pattern — sirf wahi fields update honge jo null nahi hain
public record AdminProjectUpdateRequest(
        Integer overallProgress,
        String currentStage,
        LocalDate stageStartDate,
        LocalDate stageEstCompletion,
        String nextMilestoneName,
        LocalDate nextMilestoneDate,
        Double totalBudget,
        List<TimelinePhaseUpdateRequest> phases
) {}
