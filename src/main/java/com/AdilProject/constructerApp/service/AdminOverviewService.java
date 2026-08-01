package com.AdilProject.constructerApp.service;

import com.AdilProject.constructerApp.dto.AdminOverviewResponse;
import com.AdilProject.constructerApp.entity.type.Role;
import com.AdilProject.constructerApp.repository.ProjectRepository;
import com.AdilProject.constructerApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminOverviewService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public AdminOverviewResponse getOverview() {
        long totalClients = userRepository.countByRole(Role.CLIENT);
        long activeProjects = projectRepository.count();

        // Optional.ofNullable zaroori hai: agar koi bhi project nahi hai to SQL SUM() returns NULL.
        // Bina is check ke pehle admin login pe NullPointerException aayega.
        double totalPaid = Optional.ofNullable(projectRepository.sumPaidAmount()).orElse(0.0);
        double totalBudget = Optional.ofNullable(projectRepository.sumTotalBudget()).orElse(0.0);
        double pending = totalBudget - totalPaid;

        return new AdminOverviewResponse(totalClients, activeProjects, totalPaid, pending);
    }
}
