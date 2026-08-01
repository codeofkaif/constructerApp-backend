package com.AdilProject.constructerApp.dto;

public record ProjectResponse(
        String title,
        String location,
        Integer builtUpAreaSqft,
        Integer bedrooms,
        Integer durationMonths,
        Double totalBudget
) {}