package com.homeservices.controller;

import com.homeservices.service.MarketplaceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final MarketplaceService marketplace;
    public HomeController(MarketplaceService marketplace) { this.marketplace = marketplace; }
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("categories", marketplace.getCategories());
        model.addAttribute("services", marketplace.search(null, null, null).stream().limit(6).toList());
        return "home";
    }
    @GetMapping("/access-denied")
    public String accessDenied() { return "access-denied"; }
}
