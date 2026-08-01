package com.AdilProject.constructerApp.service;

import com.AdilProject.constructerApp.dto.AdminProjectListItem;
import com.AdilProject.constructerApp.dto.AdminProjectUpdateRequest;
import com.AdilProject.constructerApp.dto.TimelinePhaseUpdateRequest;
import com.AdilProject.constructerApp.entity.Project;
import com.AdilProject.constructerApp.entity.TimeLinePhase;
import com.AdilProject.constructerApp.exception.ResourceNotFoundException;
import com.AdilProject.constructerApp.repository.ProjectRepository;
import com.AdilProject.constructerApp.repository.TimeLinePhaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminProjectService {

    private final ProjectRepository projectRepository;
    private final TimeLinePhaseRepository timeLinePhaseRepository;

    public List<AdminProjectListItem> listAll() {
        return projectRepository.findAllForAdminList();
    }

    // Partial update pattern — sirf non-null fields update honge
    // @Transactional + dirty checking: save() ki zaroorat nahi entities ke liye
    @Transactional
    public void updateProject(Long projectId, AdminProjectUpdateRequest req) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (req.overallProgress() != null) project.setOverallProgress(req.overallProgress());
        if (req.currentStage() != null) project.setCurrentStage(req.currentStage());
        if (req.stageStartDate() != null) project.setStageStartDate(req.stageStartDate());
        if (req.stageEstCompletion() != null) project.setStageEstCompletion(req.stageEstCompletion());
        if (req.nextMilestoneName() != null) project.setNextMilestoneName(req.nextMilestoneName());
        if (req.nextMilestoneDate() != null) project.setNextMilestoneDate(req.nextMilestoneDate());
        if (req.totalBudget() != null) project.setTotalBudget(req.totalBudget());

        if (req.phases() != null) {
            for (TimelinePhaseUpdateRequest ph : req.phases()) {
                TimeLinePhase phase = timeLinePhaseRepository.findById(ph.id())
                        .orElseThrow(() -> new ResourceNotFoundException("Phase not found"));
                if (ph.status() != null) phase.setStatus(ph.status());
                if (ph.percent() != null) phase.setPercent(ph.percent());
            }
        }
    }
}
