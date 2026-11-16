package com.homeservices.controller;

import com.homeservices.entity.AppUser;
import com.homeservices.enums.ProviderStatus;
import com.homeservices.service.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequestMapping("/provider")
public class ProviderController {
    private final ProviderService providerService;
    private final BookingService bookingService;
    private final MarketplaceService marketplace;
    private final ReviewService reviewService;
    public ProviderController(ProviderService providerService, BookingService bookingService, MarketplaceService marketplace, ReviewService reviewService) {
        this.providerService = providerService; this.bookingService = bookingService; this.marketplace = marketplace; this.reviewService = reviewService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal AppUser user, Model model) {
        var profile = providerService.profileFor(user);
        model.addAttribute("profile", profile);
        if (profile.getStatus() != ProviderStatus.APPROVED) return "provider/pending";
        model.addAttribute("services", providerService.getProviderServices(user));
        model.addAttribute("slots", providerService.getUpcomingSlots(user));
        model.addAttribute("bookings", bookingService.providerBookings(user));
        model.addAttribute("categories", marketplace.getCategories());
        model.addAttribute("reviews", reviewService.forProvider(profile.getId()));
        model.addAttribute("averageRating", reviewService.averageForProvider(profile.getId()));
        return "provider/dashboard";
    }

    @PostMapping("/services")
    public String addService(@AuthenticationPrincipal AppUser user, @RequestParam Long categoryId,
                             @RequestParam String title, @RequestParam(required = false) String description,
                             @RequestParam BigDecimal price, @RequestParam(defaultValue = "60") Integer durationMinutes,
                             RedirectAttributes redirect) {
        try { providerService.addService(user, categoryId, title, description, price, durationMinutes); redirect.addFlashAttribute("success", "Service listing created."); }
        catch (IllegalArgumentException | IllegalStateException ex) { redirect.addFlashAttribute("error", ex.getMessage()); }
        return "redirect:/provider/dashboard";
    }

    @PostMapping("/availability")
    public String addAvailability(@AuthenticationPrincipal AppUser user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            RedirectAttributes redirect) {
        try { providerService.addSlot(user, date, startTime, endTime); redirect.addFlashAttribute("success", "Availability slot added."); }
        catch (IllegalArgumentException | IllegalStateException ex) { redirect.addFlashAttribute("error", ex.getMessage()); }
        return "redirect:/provider/dashboard";
    }

    @PostMapping("/bookings/{id}/accept")
    public String accept(@PathVariable Long id, @AuthenticationPrincipal AppUser user, RedirectAttributes redirect) {
        try { bookingService.providerAccept(id, user); redirect.addFlashAttribute("success", "Booking confirmed."); }
        catch (IllegalArgumentException | IllegalStateException ex) { redirect.addFlashAttribute("error", ex.getMessage()); }
        return "redirect:/provider/dashboard";
    }
    @PostMapping("/bookings/{id}/reject")
    public String reject(@PathVariable Long id, @AuthenticationPrincipal AppUser user, RedirectAttributes redirect) {
        try { bookingService.providerReject(id, user); redirect.addFlashAttribute("success", "Booking rejected and its slot released."); }
        catch (IllegalArgumentException | IllegalStateException ex) { redirect.addFlashAttribute("error", ex.getMessage()); }
        return "redirect:/provider/dashboard";
    }
    @PostMapping("/availability/{id}/toggle")
    public String toggleAvailability(@PathVariable Long id, @AuthenticationPrincipal AppUser user,
                                     @RequestParam boolean available, RedirectAttributes redirect) {
        try { providerService.setSlotAvailable(user, id, available); redirect.addFlashAttribute("success", "Availability updated."); }
        catch (IllegalArgumentException | IllegalStateException ex) { redirect.addFlashAttribute("error", ex.getMessage()); }
        return "redirect:/provider/dashboard";
    }
    @PostMapping("/bookings/{id}/start")
    public String start(@PathVariable Long id, @AuthenticationPrincipal AppUser user, RedirectAttributes redirect) {
        try { bookingService.providerStart(id, user); redirect.addFlashAttribute("success", "Booking marked in progress."); }
        catch (IllegalArgumentException | IllegalStateException ex) { redirect.addFlashAttribute("error", ex.getMessage()); }
        return "redirect:/provider/dashboard";
    }
    @PostMapping("/bookings/{id}/complete")
    public String complete(@PathVariable Long id, @AuthenticationPrincipal AppUser user, RedirectAttributes redirect) {
        try { bookingService.providerComplete(id, user); redirect.addFlashAttribute("success", "Booking marked as completed."); }
        catch (IllegalArgumentException | IllegalStateException ex) { redirect.addFlashAttribute("error", ex.getMessage()); }
        return "redirect:/provider/dashboard";
    }
}
