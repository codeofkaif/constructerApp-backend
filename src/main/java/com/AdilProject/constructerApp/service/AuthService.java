package com.AdilProject.constructerApp.service;

import com.AdilProject.constructerApp.dto.AuthResponse;
import com.AdilProject.constructerApp.dto.LoginRequest;
import com.AdilProject.constructerApp.dto.RegisterRequest;
import com.AdilProject.constructerApp.entity.User;
import com.AdilProject.constructerApp.entity.type.Role;
import com.AdilProject.constructerApp.repository.UserRepository;
import com.AdilProject.constructerApp.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.findByEmail(req.email()).isPresent()) {
            throw new IllegalArgumentException("Email already registered.");
        }
        User user = User.builder()
                .name(req.name())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .phone(req.phone())
                .role(Role.CLIENT)
                .build();
        userRepository.save(user);
        String token = jwtUtil.generatetoken(user.getEmail());
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid Email of Password"));
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid Email of Password");
        }
        String token = jwtUtil.generatetoken(user.getEmail());
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }

    // Sirf existing ADMIN call kar sakta hai — SecurityConfig se /api/auth/admin/register secure hai
    public AuthResponse registerAdmin(RegisterRequest req) {
        if (userRepository.findByEmail(req.email()).isPresent()) {
            throw new IllegalArgumentException("Email already registered.");
        }
        User admin = User.builder()
                .name(req.name())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .phone(req.phone())
                .role(Role.ADMIN)
                .build();
        userRepository.save(admin);
        String token = jwtUtil.generatetoken(admin.getEmail());
        return new AuthResponse(token, admin.getId(), admin.getName(), admin.getEmail(), admin.getRole().name());
    }
}
