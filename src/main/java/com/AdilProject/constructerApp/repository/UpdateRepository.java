package com.AdilProject.constructerApp.repository;

import com.AdilProject.constructerApp.entity.Update;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

public interface UpdateRepository extends JpaRepository<Update, Long> {
    Page<Update> findByProjectIdOrderByCreatedAtDesc(Long projectId, Pageable pageable);
}