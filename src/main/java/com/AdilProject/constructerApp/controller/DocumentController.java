package com.AdilProject.constructerApp.controller;

import com.AdilProject.constructerApp.dto.DocumentResponse;
import com.AdilProject.constructerApp.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> list(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(documentService.getDocuments(user.getUsername()));
    }
}
