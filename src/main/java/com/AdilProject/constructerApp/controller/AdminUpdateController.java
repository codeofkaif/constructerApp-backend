package com.AdilProject.constructerApp.controller;

import com.AdilProject.constructerApp.dto.AdminPostUpdateRequest;
import com.AdilProject.constructerApp.service.AdminUpdateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/updates")
@RequiredArgsConstructor
public class AdminUpdateController {

    private final AdminUpdateService adminUpdateService;

    @PostMapping
    public ResponseEntity<Void> post(@Valid @RequestBody AdminPostUpdateRequest req) {
        adminUpdateService.postUpdate(req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
