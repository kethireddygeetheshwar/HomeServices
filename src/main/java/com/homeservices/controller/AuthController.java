package com.homeservices.controller;

import com.homeservices.dto.RegistrationForm;
import com.homeservices.service.RegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    private final RegistrationService registration;
    public AuthController(RegistrationService registration) { this.registration = registration; }

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/register")
    public String customerRegister(Model model) { model.addAttribute("role", "CUSTOMER"); model.addAttribute("form", new RegistrationForm()); return "register"; }

    @GetMapping("/register/provider")
    public String providerRegister(Model model) { model.addAttribute("role", "PROVIDER"); model.addAttribute("form", new RegistrationForm()); return "register"; }

    @PostMapping("/register")
    public String register(@ModelAttribute("form") RegistrationForm form, @RequestParam String role,
                           Model model, RedirectAttributes redirect) {
        model.addAttribute("role", role);
        try {
            if (!"CUSTOMER".equalsIgnoreCase(role) && !"PROVIDER".equalsIgnoreCase(role)) {
                throw new IllegalArgumentException("Choose a valid account type.");
            }
            if ("PROVIDER".equalsIgnoreCase(role)) {
                if (isBlank(form.getBusinessName()) || isBlank(form.getCity()))
                    throw new IllegalArgumentException("Business name and city are required for provider registration.");
                registration.registerProvider(form.getFullName(), form.getEmail(), form.getPhone(), form.getPassword(),
                    form.getBusinessName(), form.getDescription(), form.getExperienceYears(),
                    form.getCity(), form.getArea(), form.getAddress());
                redirect.addFlashAttribute("success", "Provider account submitted. You can sign in after registration; admin approval is required before listing services.");
            } else {
                registration.registerCustomer(form.getFullName(), form.getEmail(), form.getPhone(), form.getPassword());
                redirect.addFlashAttribute("success", "Your customer account has been created. Please sign in.");
            }
            return "redirect:/login";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("error", ex.getMessage());
            return "register";
        }
    }
    private boolean isBlank(String value) { return value == null || value.isBlank(); }
}
