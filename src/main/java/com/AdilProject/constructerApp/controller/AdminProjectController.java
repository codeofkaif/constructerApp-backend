package com.AdilProject.constructerApp.controller;

import com.AdilProject.constructerApp.dto.AdminProjectListItem;
import com.AdilProject.constructerApp.dto.AdminProjectUpdateRequest;
import com.AdilProject.constructerApp.service.AdminProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/projects")
@RequiredArgsConstructor
public class AdminProjectController {

    private final AdminProjectService adminProjectService;

    @GetMapping
    public ResponseEntity<List<AdminProjectListItem>> listAll() {
        return ResponseEntity.ok(adminProjectService.listAll());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable Long id,
            @RequestBody AdminProjectUpdateRequest req) {
        adminProjectService.updateProject(id, req);
        return ResponseEntity.noContent().build();
    }
}
