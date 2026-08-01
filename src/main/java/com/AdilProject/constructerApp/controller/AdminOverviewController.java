package com.AdilProject.constructerApp.controller;

import com.AdilProject.constructerApp.dto.AdminOverviewResponse;
import com.AdilProject.constructerApp.service.AdminOverviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/overview")
@RequiredArgsConstructor
public class AdminOverviewController {

    private final AdminOverviewService adminOverviewService;

    @GetMapping
    public ResponseEntity<AdminOverviewResponse> overview() {
        return ResponseEntity.ok(adminOverviewService.getOverview());
    }
}
