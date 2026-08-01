package com.AdilProject.constructerApp.repository;

import com.AdilProject.constructerApp.entity.TimeLinePhase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimeLinePhaseRepository extends JpaRepository<TimeLinePhase, Long> {
    List<TimeLinePhase> findByProjectIdOrderBySortOrderAsc(Long projectId);
}
