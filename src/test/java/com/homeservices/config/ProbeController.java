package com.homeservices.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Test-only controller used to verify URL authorization and CSRF behavior.
 * It is deliberately a top-level public controller so @WebMvcTest reliably
 * registers its handler mappings. It is not included in the production JAR.
 */
@RestController
public class ProbeController {
    @GetMapping("/admin/dashboard")
    public String adminPage() { return "admin"; }

    @GetMapping("/provider/dashboard")
    public String providerPage() { return "provider"; }

    @GetMapping("/api/v1/admin/probe")
    public String adminApi() { return "admin"; }

    @PostMapping("/api/v1/bookings/probe")
    public String customerBookingApi() { return "created"; }
}
