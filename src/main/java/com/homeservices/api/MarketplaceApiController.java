package com.homeservices.api;

import com.homeservices.entity.ServiceCategory;
import com.homeservices.entity.ServiceOffering;
import com.homeservices.service.MarketplaceService;
import com.homeservices.service.ProviderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class MarketplaceApiController {
    private final MarketplaceService marketplace;
    private final ProviderService providers;
    public MarketplaceApiController(MarketplaceService marketplace, ProviderService providers) {
        this.marketplace = marketplace; this.providers = providers;
    }

    @GetMapping("/categories")
    public List<CategoryResponse> categories() {
        return marketplace.getCategories().stream().map(c -> new CategoryResponse(c.getId(), c.getName(), c.getSlug(), c.getDescription(), c.getIcon())).toList();
    }

    @GetMapping("/services")
    public List<ServiceResponse> services(@RequestParam(required = false) String city,
                                          @RequestParam(required = false) String category,
                                          @RequestParam(required = false) String q) {
        return marketplace.search(city, category, q).stream().map(this::mapService).toList();
    }

    @GetMapping("/providers/{providerId}/availability")
    public List<SlotResponse> availability(@PathVariable Long providerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Integer durationMinutes) {
        return providers.getAvailableSlots(providerId, date, durationMinutes).stream()
                .map(s -> new SlotResponse(s.getId(), s.getSlotDate(), s.getStartTime(), s.getEndTime(), s.isAvailable())).toList();
    }

    private ServiceResponse mapService(ServiceOffering s) {
        return new ServiceResponse(s.getId(), s.getTitle(), s.getDescription(), s.getPrice(), s.getDurationMinutes(),
                s.getCategory().getName(), s.getProvider().getId(), s.getProvider().getBusinessName(),
                s.getProvider().getCity(), s.getProvider().getArea());
    }
    public record CategoryResponse(Long id, String name, String slug, String description, String icon) { }
    public record ServiceResponse(Long id, String title, String description, java.math.BigDecimal price,
                                  Integer durationMinutes, String category, Long providerId,
                                  String providerName, String city, String area) { }
    public record SlotResponse(Long id, LocalDate date, java.time.LocalTime startTime,
                               java.time.LocalTime endTime, boolean available) { }
}
