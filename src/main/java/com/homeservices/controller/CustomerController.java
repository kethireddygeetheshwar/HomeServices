package com.homeservices.controller;

import com.homeservices.entity.AppUser;
import com.homeservices.service.BookingService;
import com.homeservices.service.ReviewService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customer")
public class CustomerController {
    private final BookingService bookingService;
    private final ReviewService reviewService;
    public CustomerController(BookingService bookingService, ReviewService reviewService) {
        this.bookingService = bookingService; this.reviewService = reviewService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal AppUser user, Model model) {
        var bookings = bookingService.customerBookings(user);
        model.addAttribute("user", user); model.addAttribute("bookings", bookings.stream().limit(5).toList());
        model.addAttribute("bookingCount", bookings.size());
        return "customer/dashboard";
    }

    @GetMapping("/bookings")
    public String bookings(@AuthenticationPrincipal AppUser user, Model model) {
        var customerBookings = bookingService.customerBookings(user);
        model.addAttribute("bookings", customerBookings);
        model.addAttribute("reviewedBookingIds", customerBookings.stream()
                .filter(booking -> reviewService.hasReview(booking.getId()))
                .map(booking -> booking.getId()).collect(java.util.stream.Collectors.toSet()));
        return "customer/bookings";
    }

    @PostMapping("/bookings")
    public String createBooking(@AuthenticationPrincipal AppUser user, @RequestParam Long serviceId,
                                @RequestParam Long slotId, @RequestParam(required = false) String notes,
                                RedirectAttributes redirect) {
        try {
            var booking = bookingService.createBooking(user, serviceId, slotId, notes);
            redirect.addFlashAttribute("success", "Booking request #" + booking.getId() + " submitted. The provider must confirm it.");
            return "redirect:/customer/bookings";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/services/" + serviceId;
        }
    }

    @PostMapping("/bookings/{id}/cancel")
    public String cancel(@PathVariable Long id, @AuthenticationPrincipal AppUser user, RedirectAttributes redirect) {
        try { bookingService.cancelByCustomer(id, user); redirect.addFlashAttribute("success", "Booking cancelled."); }
        catch (IllegalArgumentException | IllegalStateException ex) { redirect.addFlashAttribute("error", ex.getMessage()); }
        return "redirect:/customer/bookings";
    }

    @PostMapping("/bookings/{id}/review")
    public String review(@PathVariable Long id, @AuthenticationPrincipal AppUser user,
                         @RequestParam Integer rating, @RequestParam(required = false) String comment,
                         RedirectAttributes redirect) {
        try { bookingService.addReview(id, user, rating, comment); redirect.addFlashAttribute("success", "Thank you for your review."); }
        catch (IllegalArgumentException | IllegalStateException ex) { redirect.addFlashAttribute("error", ex.getMessage()); }
        return "redirect:/customer/bookings";
    }
}
