package com.AdilProject.constructerApp.controller;

import com.AdilProject.constructerApp.dto.PortfolioItemDto;
import com.AdilProject.constructerApp.dto.SiteServiceDto;
import com.AdilProject.constructerApp.service.AdminContentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicContentController {

    private final AdminContentService contentService;
    private final ObjectMapper objectMapper;

    @GetMapping("/portfolio")
    public List<PortfolioItemDto> getPortfolio() {
        return contentService.getAllPortfolio();
    }

    @GetMapping("/services")
    public List<SiteServiceDto> getServices() {
        return contentService.getAllServices();
    }

    @GetMapping("/config/{key}")
    public ResponseEntity<JsonNode> getConfig(@PathVariable String key) throws Exception {
        String value = contentService.getConfig(key);
        if (value == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(objectMapper.readTree(value));
    }
}
