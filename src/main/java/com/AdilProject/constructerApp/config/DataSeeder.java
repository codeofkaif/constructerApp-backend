package com.AdilProject.constructerApp.config;

import com.AdilProject.constructerApp.entity.User;
import com.AdilProject.constructerApp.entity.type.Role;
import com.AdilProject.constructerApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.bootstrap.name}")
    private String adminName;

    @Value("${admin.bootstrap.email}")
    private String adminEmail;

    @Value("${admin.bootstrap.password}")
    private String adminPassword;

    @Value("${admin.bootstrap.phone}")
    private String adminPhone;

    @Override
    public void run(String... args) {
        // Agar koi bhi ADMIN DB mein nahi hai tabhi seed karo
        boolean adminExists = userRepository.findByEmail(adminEmail).isPresent();

        if (!adminExists) {
            User admin = User.builder()
                    .name(adminName)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .phone(adminPhone)
                    .role(Role.ADMIN)
                    .build();

            userRepository.save(admin);
            log.info("✅ Bootstrap Admin created: {}", adminEmail);
        } else {
            log.info("ℹ️  Admin already exists, skipping seed.");
        }
    }
}
