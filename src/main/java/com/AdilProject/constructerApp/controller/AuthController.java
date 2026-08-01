package com.AdilProject.constructerApp.controller;

import com.AdilProject.constructerApp.dto.AuthResponse;
import com.AdilProject.constructerApp.dto.LoginRequest;
import com.AdilProject.constructerApp.dto.RegisterRequest;
import com.AdilProject.constructerApp.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    // Sirf existing ADMIN call kar sakta hai — Bearer token zaroori hai
    @PostMapping("/admin/register")
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerAdmin(req));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDetails> me(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userDetails);
    }
}
