package com.AdilProject.constructerApp.entity;

import com.AdilProject.constructerApp.entity.type.PaymentMethod;
import com.AdilProject.constructerApp.entity.type.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.SpringVersion;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double amount;
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private LocalDate dueDate;
    private LocalDateTime paidAt;

    @ManyToOne
    @JoinColumn(name="project_id")
    private Project project;
}
