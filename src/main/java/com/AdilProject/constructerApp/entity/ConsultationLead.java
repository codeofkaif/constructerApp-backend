package com.AdilProject.constructerApp.entity;

import com.AdilProject.constructerApp.entity.type.LeadStatus;
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
public class ConsultationLead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phone;
    private String message;    // fixed: was 'massage'
    private LocalDateTime createdAt;  // fixed: was 'createAt'

    @Enumerated(EnumType.STRING)
    private LeadStatus status;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();  // fixed: was this.createAt
        this.status = LeadStatus.NEW;
    }
}
