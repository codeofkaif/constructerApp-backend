package com.AdilProject.constructerApp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "site_configs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteConfig {

    @Id
    @Column(nullable = false, unique = true)
    private String configKey;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String configValue;

    @UpdateTimestamp
    private Instant updatedAt;
}
