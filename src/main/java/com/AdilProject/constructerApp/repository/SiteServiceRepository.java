package com.AdilProject.constructerApp.repository;

import com.AdilProject.constructerApp.entity.SiteService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SiteServiceRepository extends JpaRepository<SiteService, Long> {
    List<SiteService> findAllByOrderBySortOrderAsc();
}
