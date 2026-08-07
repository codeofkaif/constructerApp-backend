package com.AdilProject.constructerApp.controller;

import com.AdilProject.constructerApp.dto.PortfolioItemDto;
import com.AdilProject.constructerApp.dto.SiteServiceDto;
import com.AdilProject.constructerApp.service.AdminContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicContentController {

    private final AdminContentService contentService;

    @GetMapping("/portfolio")
    public List<PortfolioItemDto> getPortfolio() {
        return contentService.getAllPortfolio();
    }

    @GetMapping("/services")
    public List<SiteServiceDto> getServices() {
        return contentService.getAllServices();
    }

    @GetMapping("/config/{key}")
    public ResponseEntity<String> getConfig(@PathVariable String key) {
        String value = contentService.getConfig(key);
        if (value == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(value);
    }
}
