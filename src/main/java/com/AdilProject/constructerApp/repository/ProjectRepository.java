package com.AdilProject.constructerApp.repository;

import com.AdilProject.constructerApp.dto.AdminProjectListItem;
import com.AdilProject.constructerApp.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByOwnerId(Long ownerId);

    // Section 8 — DB level SUM (N+1 problem se bachne ke liye Java loop se nahi karna)
    // NULL return karta hai agar koi project nahi hai — service mein Optional.ofNullable se handle kiya
    @Query("SELECT SUM(p.paidAmount) FROM Project p")
    Double sumPaidAmount();

    @Query("SELECT SUM(p.totalBudget) FROM Project p")
    Double sumTotalBudget();

    // JPQL constructor expression — seedha owner.name tak join, N+1 nahi banata
    // Field order AdminProjectListItem record ke fields se exactly match karna chahiye
    @Query("SELECT new com.AdilProject.constructerApp.dto.AdminProjectListItem(" +
           "p.id, p.owner.name, p.title, p.location, p.overallProgress, " +
           "p.currentStage, p.totalBudget, p.paidAmount) FROM Project p")
    List<AdminProjectListItem> findAllForAdminList();
}