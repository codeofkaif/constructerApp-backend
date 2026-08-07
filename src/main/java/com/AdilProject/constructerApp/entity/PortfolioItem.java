package com.AdilProject.constructerApp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "portfolio_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String slug;

    private String location;

    @Column(nullable = false)
    @Builder.Default
    private boolean featured = false;

    // JSON array of {value, label} stored as text
    @Column(columnDefinition = "TEXT")
    private String statsJson;

    // JSON array of {url, alt} stored as text
    @Column(columnDefinition = "TEXT")
    private String imagesJson;

    @Builder.Default
    private int sortOrder = 0;

    @CreationTimestamp
    private Instant createdAt;
}
