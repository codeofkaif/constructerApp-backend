package com.AdilProject.constructerApp.entity;

import com.AdilProject.constructerApp.entity.type.PhaseStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeLinePhase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private PhaseStatus status;

    private Integer percent;
    private Integer sortOrder;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
}
