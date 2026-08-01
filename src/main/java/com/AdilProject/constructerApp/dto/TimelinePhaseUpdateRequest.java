package com.AdilProject.constructerApp.dto;

import com.AdilProject.constructerApp.entity.type.PhaseStatus;

public record TimelinePhaseUpdateRequest(
        Long id,
        PhaseStatus status,
        Integer percent
) {}
