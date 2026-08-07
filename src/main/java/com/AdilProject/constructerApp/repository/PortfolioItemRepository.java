package com.AdilProject.constructerApp.repository;

import com.AdilProject.constructerApp.entity.PortfolioItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, Long> {
    List<PortfolioItem> findAllByOrderBySortOrderAscCreatedAtDesc();
}
