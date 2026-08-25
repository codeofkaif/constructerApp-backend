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

    // Config — returns stored JSON as a raw Java Map (Spring MVC serializes it to JSON automatically)
    @GetMapping("/config/{key}")
    public ResponseEntity<Object> getConfig(@PathVariable String key) {
        Object config = contentService.getConfigAsMap(key);
        if (config == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(config);
    }
}
