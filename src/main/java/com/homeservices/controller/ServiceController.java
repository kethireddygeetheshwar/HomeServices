package com.homeservices.controller;

import com.homeservices.service.MarketplaceService;
import com.homeservices.service.ProviderService;
import com.homeservices.service.ReviewService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@Controller
public class ServiceController {
    private final MarketplaceService marketplace;
    private final ProviderService providers;
    private final ReviewService reviews;
    public ServiceController(MarketplaceService marketplace, ProviderService providers, ReviewService reviews) { this.marketplace = marketplace; this.providers = providers; this.reviews = reviews; }

    @GetMapping("/services")
    public String services(@RequestParam(required = false) String city,
                           @RequestParam(required = false) String category,
                           @RequestParam(required = false) String q, Model model) {
        model.addAttribute("categories", marketplace.getCategories());
        model.addAttribute("services", marketplace.search(city, category, q));
        model.addAttribute("city", city); model.addAttribute("category", category); model.addAttribute("q", q);
        return "services";
    }

    @GetMapping("/services/{id}")
    public String serviceDetail(@PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {
        var service = marketplace.getPublicService(id);
        LocalDate selectedDate = date == null ? LocalDate.now() : date;
        model.addAttribute("service", service);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("requiredDurationMinutes", service.getDurationMinutes());
        model.addAttribute("slots", providers.getAvailableSlots(
                service.getProvider().getId(), selectedDate, service.getDurationMinutes()));
        model.addAttribute("reviews", reviews.forProvider(service.getProvider().getId()));
        model.addAttribute("averageRating", reviews.averageForProvider(service.getProvider().getId()));
        return "service-detail";
    }
}
