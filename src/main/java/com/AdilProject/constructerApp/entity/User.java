package com.AdilProject.constructerApp.entity;

import com.AdilProject.constructerApp.entity.type.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email ;

    @Column(nullable = false)
    private String password;

    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String avatarURL;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "owner",cascade = CascadeType.ALL , orphanRemoval=true)
    private Project project;

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
    }









}
