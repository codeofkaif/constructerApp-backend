package com.AdilProject.constructerApp.controller;

import com.AdilProject.constructerApp.entity.ConsultationLead;
import com.AdilProject.constructerApp.entity.type.LeadStatus;
import com.AdilProject.constructerApp.exception.ResourceNotFoundException;
import com.AdilProject.constructerApp.repository.ConsultationLeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/leads")
@RequiredArgsConstructor
public class AdminLeadController {

    private final ConsultationLeadRepository leadRepository;

    // Paginated list of all consultation leads
    @GetMapping
    public ResponseEntity<Page<ConsultationLead>> all(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(leadRepository.findAll(PageRequest.of(page, size)));
    }

    // Section 8.6 — Status dropdown ke liye (NEW → CONTACTED → CLOSED)
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam LeadStatus status) {
        ConsultationLead lead = leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found"));
        lead.setStatus(status);
        leadRepository.save(lead);
        return ResponseEntity.noContent().build();
    }
}
