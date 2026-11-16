package com.homeservices.controller;

import com.homeservices.enums.ProviderStatus;
import com.homeservices.service.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final AdminService admin;
    public AdminController(AdminService admin) { this.admin = admin; }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("customers", admin.customerCount());
        model.addAttribute("providers", admin.providerCount());
        model.addAttribute("pendingProviders", admin.pendingProviderCount());
        model.addAttribute("services", admin.serviceCount());
        model.addAttribute("bookings", admin.bookingCount());
        model.addAttribute("reviews", admin.reviewCount());
        model.addAttribute("pending", admin.pendingProviders());
        return "admin/dashboard";
    }

    @PostMapping("/providers/{id}/approve")
    public String approve(@PathVariable Long id, RedirectAttributes redirect) {
        try { admin.updateProviderStatus(id, ProviderStatus.APPROVED); redirect.addFlashAttribute("success", "Provider approved."); }
        catch (IllegalArgumentException | IllegalStateException ex) { redirect.addFlashAttribute("error", ex.getMessage()); }
        return "redirect:/admin/dashboard";
    }
    @PostMapping("/providers/{id}/reject")
    public String reject(@PathVariable Long id, RedirectAttributes redirect) {
        try { admin.updateProviderStatus(id, ProviderStatus.REJECTED); redirect.addFlashAttribute("success", "Provider rejected."); }
        catch (IllegalArgumentException | IllegalStateException ex) { redirect.addFlashAttribute("error", ex.getMessage()); }
        return "redirect:/admin/dashboard";
    }
}
