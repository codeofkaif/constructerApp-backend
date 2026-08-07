package com.AdilProject.constructerApp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "site_services")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String iconName;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, unique = true)
    private String slug;

    @Builder.Default
    private int sortOrder = 0;
}
