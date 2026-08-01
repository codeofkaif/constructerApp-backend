package com.AdilProject.constructerApp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.parameters.P;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Update {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    private String title;
    private String thumbnailUrl;
    private String description;
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @PrePersist
    private void oneCreat(){
        this.createdAt = LocalDateTime.now();
    }


}
