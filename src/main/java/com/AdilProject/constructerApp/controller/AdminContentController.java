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
import java.util.Map;

@RestController
@RequestMapping("/api/admin/content")
@RequiredArgsConstructor
public class AdminContentController {

    private final AdminContentService contentService;
    private final ObjectMapper objectMapper;

    // Portfolio
    @GetMapping("/portfolio")
    public List<PortfolioItemDto> listPortfolio() { return contentService.getAllPortfolio(); }

    @PostMapping("/portfolio")
    public PortfolioItemDto createPortfolio(@RequestBody PortfolioItemDto req) { return contentService.createPortfolio(req); }

    @PutMapping("/portfolio/{id}")
    public PortfolioItemDto updatePortfolio(@PathVariable Long id, @RequestBody PortfolioItemDto req) { return contentService.updatePortfolio(id, req); }

    @DeleteMapping("/portfolio/{id}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable Long id) { contentService.deletePortfolio(id); return ResponseEntity.noContent().build(); }

    @PatchMapping("/portfolio/{id}/featured")
    public PortfolioItemDto toggleFeatured(@PathVariable Long id) { return contentService.toggleFeatured(id); }

    // Services
    @GetMapping("/services")
    public List<SiteServiceDto> listServices() { return contentService.getAllServices(); }

    @PostMapping("/services")
    public SiteServiceDto createService(@RequestBody SiteServiceDto req) { return contentService.createService(req); }

    @PutMapping("/services/{id}")
    public SiteServiceDto updateService(@PathVariable Long id, @RequestBody SiteServiceDto req) { return contentService.updateService(id, req); }

    @DeleteMapping("/services/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) { contentService.deleteService(id); return ResponseEntity.noContent().build(); }

    @PutMapping("/services/reorder")
    public ResponseEntity<Void> reorderServices(@RequestBody List<Map<String, Object>> order) { contentService.reorderServices(order); return ResponseEntity.noContent().build(); }

    // Config — stored as JSON, sent/received as JSON (no string wrapping)
    @GetMapping("/config/{key}")
    public ResponseEntity<JsonNode> getConfig(@PathVariable String key) throws Exception {
        String value = contentService.getConfig(key);
        if (value == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(objectMapper.readTree(value));
    }

    @PutMapping("/config/{key}")
    public ResponseEntity<Void> saveConfig(@PathVariable String key,
                                           @RequestBody JsonNode value) throws Exception {
        contentService.saveConfig(key, objectMapper.writeValueAsString(value));
        return ResponseEntity.noContent().build();
    }
}
