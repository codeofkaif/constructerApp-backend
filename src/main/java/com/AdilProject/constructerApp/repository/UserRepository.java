package com.AdilProject.constructerApp.repository;

import com.AdilProject.constructerApp.entity.User;
import com.AdilProject.constructerApp.entity.type.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    // Section 8 ke liye — Admin notification broadcast
    List<User> findByRole(Role role);

    // Section 8 ke liye — Admin overview metrics (DB level count)
    long countByRole(Role role);
}