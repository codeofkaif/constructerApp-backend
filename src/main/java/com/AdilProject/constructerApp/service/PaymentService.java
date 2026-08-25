package com.AdilProject.constructerApp.service;

import com.AdilProject.constructerApp.dto.PayRequest;
import com.AdilProject.constructerApp.dto.PaymentSummaryResponse;
import com.AdilProject.constructerApp.entity.Payment;
import com.AdilProject.constructerApp.entity.Project;
import com.AdilProject.constructerApp.entity.User;
import com.AdilProject.constructerApp.entity.type.PaymentStatus;
import com.AdilProject.constructerApp.exception.ResourceNotFoundException;
import com.AdilProject.constructerApp.repository.PaymentRepository;
import com.AdilProject.constructerApp.repository.ProjectRepository;
import com.AdilProject.constructerApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ProjectRepository projectRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Transactional
    public PaymentSummaryResponse pay(String email, PayRequest req) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Project project = projectRepository.findByOwnerId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        double remaining = project.getTotalBudget() - project.getPaidAmount();
        if (req.amount() > remaining) {
            throw new IllegalArgumentException("Amount exceeds remaining balance");
        }

        Payment payment = Payment.builder()
                .amount(req.amount())
                .method(req.method())
                .status(PaymentStatus.SUCCESS)
                .paidAt(LocalDateTime.now())
                .project(project)
                .build();
        paymentRepository.save(payment);

        project.setPaidAmount(project.getPaidAmount() + req.amount());

        return buildSummary(project);
    }

    public PaymentSummaryResponse getSummary(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Project project = projectRepository.findByOwnerId(user.getId()).orElse(null);
        if (project == null) {
            return new PaymentSummaryResponse(0.0, 0.0, 0.0);
        }
        return buildSummary(project);
    }

    private PaymentSummaryResponse buildSummary(Project project) {
        double remaining = project.getTotalBudget() - project.getPaidAmount();
        return new PaymentSummaryResponse(project.getTotalBudget(), project.getPaidAmount(), remaining);
    }
}
