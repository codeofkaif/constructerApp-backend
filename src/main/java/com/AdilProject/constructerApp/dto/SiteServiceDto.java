package com.AdilProject.constructerApp.dto;

import lombok.Data;

@Data
public class SiteServiceDto {
    private Long id;
    private String iconName;
    private String title;
    private String description;
    private String slug;
    private int sortOrder;
}
