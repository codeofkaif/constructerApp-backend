package com.AdilProject.constructerApp.service;

import com.AdilProject.constructerApp.dto.OverviewResponse;
import com.AdilProject.constructerApp.dto.ProjectResponse;
import com.AdilProject.constructerApp.dto.TimelinePhaseResponse;
import com.AdilProject.constructerApp.dto.UpdateResponse;
import com.AdilProject.constructerApp.entity.Project;
import com.AdilProject.constructerApp.entity.User;
import com.AdilProject.constructerApp.exception.ResourceNotFoundException;
import com.AdilProject.constructerApp.repository.ProjectRepository;
import com.AdilProject.constructerApp.repository.TimeLinePhaseRepository;
import com.AdilProject.constructerApp.repository.UpdateRepository;
import com.AdilProject.constructerApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TimeLinePhaseRepository timelinePhaseRepository;
    private final UpdateRepository updateRepository;

    private Project getProjectForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return projectRepository.findByOwnerId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No project found for this user"));
    }

    public OverviewResponse getOverview(String email) {
        Project p = getProjectForUser(email);
        return new OverviewResponse(
                p.getOverallProgress(), p.getCurrentStage(),
                p.getStageStartDate(), p.getStageEstCompletion(),
                p.getNextMilestoneName(), p.getNextMilestoneDate()
        );
    }

    public List<TimelinePhaseResponse> getTimeline(String email) {
        Project p = getProjectForUser(email);
        return timelinePhaseRepository.findByProjectIdOrderBySortOrderAsc(p.getId())
                .stream()
                .map(ph -> new TimelinePhaseResponse(ph.getName(), ph.getStatus().name(), ph.getPercent()))
                .toList();
    }

    public Page<UpdateResponse> getUpdates(String email, int page, int size) {
        Project p = getProjectForUser(email);
        return updateRepository.findByProjectIdOrderByCreatedAtDesc(p.getId(), PageRequest.of(page, size))
                .map(u -> new UpdateResponse(u.getTitle(), u.getDescription(), u.getThumbnailUrl(), u.getCreatedAt()));
    }

    public ProjectResponse getProject(String email) {
        Project p = getProjectForUser(email);
        return new ProjectResponse(p.getTitle(), p.getLocation(), p.getBuiltUpAreaSqft(),
                p.getBedrooms(), p.getDurationMonths(), p.getTotalBudget());
    }
}
