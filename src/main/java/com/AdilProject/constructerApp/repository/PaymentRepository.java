package com.AdilProject.constructerApp.repository;

import com.AdilProject.constructerApp.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByProjectIdOrderByPaidAtDesc(Long projectId);
}
