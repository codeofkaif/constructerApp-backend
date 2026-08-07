package com.AdilProject.constructerApp.dto;

import lombok.Data;

@Data
public class PortfolioItemDto {
    private Long id;
    private String title;
    private String slug;
    private String location;
    private boolean featured;
    private String statsJson;   // JSON string: [{value, label}, ...]
    private String imagesJson;  // JSON string: [{url, alt}, ...]
    private int sortOrder;
    private String createdAt;
}
