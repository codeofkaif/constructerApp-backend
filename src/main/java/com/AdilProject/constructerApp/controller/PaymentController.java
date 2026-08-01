package com.AdilProject.constructerApp.controller;

import com.AdilProject.constructerApp.dto.PayRequest;
import com.AdilProject.constructerApp.dto.PaymentSummaryResponse;
import com.AdilProject.constructerApp.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<PaymentSummaryResponse> summary(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(paymentService.getSummary(user.getUsername()));
    }

    @PostMapping("/pay")
    public ResponseEntity<PaymentSummaryResponse> pay(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody PayRequest req) {
        return ResponseEntity.ok(paymentService.pay(user.getUsername(), req));
    }
}
