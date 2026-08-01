package com.AdilProject.constructerApp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileUrl;
    private LocalDateTime uploadedAt;  // fixed: was 'uploadAt'

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @PrePersist
    protected void onCreate() {
        this.uploadedAt = LocalDateTime.now();  // fixed
    }
}
