package com.AdilProject.constructerApp.controller;

import com.AdilProject.constructerApp.dto.LeadRequest;
import com.AdilProject.constructerApp.entity.ConsultationLead;
import com.AdilProject.constructerApp.repository.ConsultationLeadRepository;
import com.AdilProject.constructerApp.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
public class LeadController {

    private final ConsultationLeadRepository leadRepository;
    private final EmailService emailService;

    // Simple in-memory rate limit: IP → last-request-timestamps.
    // Production mein Redis ya Bucket4j use karna behtar hoga.
    private final Map<String, List<Long>> requestLog = new ConcurrentHashMap<>();

    @PostMapping
    public ResponseEntity<Map<String, String>> submit(
            @Valid @RequestBody LeadRequest req,
            HttpServletRequest request) {

        String ip = request.getRemoteAddr();
        long now = System.currentTimeMillis();

        List<Long> timestamps = requestLog.computeIfAbsent(ip, k -> new ArrayList<>());
        // 1 ghante se purani entries hata do
        timestamps.removeIf(t -> now - t > 3600_000);

        // Same IP se 5 se zyada requests 1 ghante mein allowed nahi
        if (timestamps.size() >= 5) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message", "Too many requests. Please try again later."));
        }
        timestamps.add(now);

        ConsultationLead lead = ConsultationLead.builder()
                .name(req.name())
                .phone(req.phone())
                .message(req.message())
                .build();
        leadRepository.save(lead);

        // Send email alert to Admin
        emailService.sendLeadNotification(lead);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Thank you! We'll contact you within 24 hours."));
    }
}

