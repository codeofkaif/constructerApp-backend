package com.AdilProject.constructerApp.dto;

public record TimelinePhaseResponse(
        String name,
        String status,
        Integer percent
) {}