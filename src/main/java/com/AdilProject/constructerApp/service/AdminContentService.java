package com.AdilProject.constructerApp.service;

import com.AdilProject.constructerApp.dto.PortfolioItemDto;
import com.AdilProject.constructerApp.dto.SiteServiceDto;
import com.AdilProject.constructerApp.entity.PortfolioItem;
import com.AdilProject.constructerApp.entity.SiteConfig;
import com.AdilProject.constructerApp.entity.SiteService;
import com.AdilProject.constructerApp.repository.PortfolioItemRepository;
import com.AdilProject.constructerApp.repository.SiteConfigRepository;
import com.AdilProject.constructerApp.repository.SiteServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminContentService {

    private final PortfolioItemRepository portfolioRepo;
    private final SiteServiceRepository serviceRepo;
    private final SiteConfigRepository configRepo;

    // -------------------------------------------------------------------------
    // Portfolio
    // -------------------------------------------------------------------------

    public List<PortfolioItemDto> getAllPortfolio() {
        return portfolioRepo.findAllByOrderBySortOrderAscCreatedAtDesc()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public PortfolioItemDto createPortfolio(PortfolioItemDto req) {
        if (req.isFeatured()) clearFeatured();
        PortfolioItem item = PortfolioItem.builder()
                .title(req.getTitle())
                .slug(req.getSlug())
                .location(req.getLocation())
                .featured(req.isFeatured())
                .statsJson(req.getStatsJson())
                .imagesJson(req.getImagesJson())
                .sortOrder(req.getSortOrder())
                .build();
        return toDto(portfolioRepo.save(item));
    }

    @Transactional
    public PortfolioItemDto updatePortfolio(Long id, PortfolioItemDto req) {
        PortfolioItem item = portfolioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Portfolio item not found: " + id));
        if (req.isFeatured()) clearFeatured();
        item.setTitle(req.getTitle());
        item.setSlug(req.getSlug());
        item.setLocation(req.getLocation());
        item.setFeatured(req.isFeatured());
        item.setStatsJson(req.getStatsJson());
        item.setImagesJson(req.getImagesJson());
        item.setSortOrder(req.getSortOrder());
        return toDto(portfolioRepo.save(item));
    }

    public void deletePortfolio(Long id) {
        portfolioRepo.deleteById(id);
    }

    @Transactional
    public PortfolioItemDto toggleFeatured(Long id) {
        clearFeatured();
        PortfolioItem item = portfolioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Portfolio item not found: " + id));
        item.setFeatured(!item.isFeatured());
        return toDto(portfolioRepo.save(item));
    }

    private void clearFeatured() {
        portfolioRepo.findAllByOrderBySortOrderAscCreatedAtDesc()
                .forEach(p -> { p.setFeatured(false); portfolioRepo.save(p); });
    }

    private PortfolioItemDto toDto(PortfolioItem p) {
        PortfolioItemDto dto = new PortfolioItemDto();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setSlug(p.getSlug());
        dto.setLocation(p.getLocation());
        dto.setFeatured(p.isFeatured());
        dto.setStatsJson(p.getStatsJson());
        dto.setImagesJson(p.getImagesJson());
        dto.setSortOrder(p.getSortOrder());
        if (p.getCreatedAt() != null) dto.setCreatedAt(p.getCreatedAt().toString());
        return dto;
    }

    // -------------------------------------------------------------------------
    // Services
    // -------------------------------------------------------------------------

    public List<SiteServiceDto> getAllServices() {
        return serviceRepo.findAllByOrderBySortOrderAsc()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public SiteServiceDto createService(SiteServiceDto req) {
        SiteService s = SiteService.builder()
                .iconName(req.getIconName())
                .title(req.getTitle())
                .description(req.getDescription())
                .slug(req.getSlug())
                .sortOrder(req.getSortOrder())
                .build();
        return toDto(serviceRepo.save(s));
    }

    @Transactional
    public SiteServiceDto updateService(Long id, SiteServiceDto req) {
        SiteService s = serviceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found: " + id));
        s.setIconName(req.getIconName());
        s.setTitle(req.getTitle());
        s.setDescription(req.getDescription());
        s.setSlug(req.getSlug());
        s.setSortOrder(req.getSortOrder());
        return toDto(serviceRepo.save(s));
    }

    public void deleteService(Long id) {
        serviceRepo.deleteById(id);
    }

    @Transactional
    public void reorderServices(List<Map<String, Object>> order) {
        order.forEach(entry -> {
            Long id = Long.valueOf(entry.get("id").toString());
            int sortOrder = Integer.parseInt(entry.get("sortOrder").toString());
            serviceRepo.findById(id).ifPresent(s -> {
                s.setSortOrder(sortOrder);
                serviceRepo.save(s);
            });
        });
    }

    private SiteServiceDto toDto(SiteService s) {
        SiteServiceDto dto = new SiteServiceDto();
        dto.setId(s.getId());
        dto.setIconName(s.getIconName());
        dto.setTitle(s.getTitle());
        dto.setDescription(s.getDescription());
        dto.setSlug(s.getSlug());
        dto.setSortOrder(s.getSortOrder());
        return dto;
    }

    // -------------------------------------------------------------------------
    // Site Config (JSON blobs for homepage/about/contact/footer)
    // -------------------------------------------------------------------------

    public String getConfig(String key) {
        return configRepo.findByConfigKey(key)
                .map(SiteConfig::getConfigValue)
                .orElse(null);
    }

    @Transactional
    public void saveConfig(String key, String value) {
        SiteConfig config = configRepo.findByConfigKey(key)
                .orElse(SiteConfig.builder().configKey(key).build());
        config.setConfigValue(value);
        configRepo.save(config);
    }
}
