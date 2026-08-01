package com.AdilProject.constructerApp.controller;

import com.AdilProject.constructerApp.dto.OverviewResponse;
import com.AdilProject.constructerApp.dto.ProjectResponse;
import com.AdilProject.constructerApp.dto.TimelinePhaseResponse;
import com.AdilProject.constructerApp.dto.UpdateResponse;
import com.AdilProject.constructerApp.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/overview")
    public ResponseEntity<OverviewResponse> overview(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(dashboardService.getOverview(user.getUsername()));
    }

    @GetMapping("/timeline")
    public ResponseEntity<List<TimelinePhaseResponse>> timeline(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(dashboardService.getTimeline(user.getUsername()));
    }

    @GetMapping("/updates")
    public ResponseEntity<Page<UpdateResponse>> updates(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(dashboardService.getUpdates(user.getUsername(), page, size));
    }

    @GetMapping("/project")
    public ResponseEntity<ProjectResponse> project(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(dashboardService.getProject(user.getUsername()));
    }
}