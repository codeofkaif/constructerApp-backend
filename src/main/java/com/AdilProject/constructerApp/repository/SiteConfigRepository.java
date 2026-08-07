package com.AdilProject.constructerApp.repository;

import com.AdilProject.constructerApp.entity.SiteConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SiteConfigRepository extends JpaRepository<SiteConfig, String> {
    Optional<SiteConfig> findByConfigKey(String configKey);
}
