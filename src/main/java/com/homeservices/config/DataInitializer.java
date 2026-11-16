package com.homeservices.config;

import com.homeservices.entity.AppUser;
import com.homeservices.entity.ServiceCategory;
import com.homeservices.enums.Role;
import com.homeservices.repository.AppUserRepository;
import com.homeservices.repository.ServiceCategoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seedApplication(AppUserRepository users, ServiceCategoryRepository categories,
                                      PasswordEncoder encoder, Environment environment,
                                      @Value("${app.bootstrap.admin-email}") String adminEmail,
                                      @Value("${app.bootstrap.admin-password}") String adminPassword) {
        return args -> {
            if (environment.acceptsProfiles(Profiles.of("prod")) && adminPassword.length() < 16) {
                throw new IllegalStateException("ADMIN_PASSWORD must contain at least 16 characters when the prod profile is active.");
            }
            if (!users.existsByEmailIgnoreCase(adminEmail)) {
                AppUser admin = new AppUser();
                admin.setFullName("Platform Administrator");
                admin.setEmail(adminEmail.toLowerCase());
                admin.setPassword(encoder.encode(adminPassword));
                admin.setRole(Role.ADMIN);
                admin.setEnabled(true);
                users.save(admin);
            }
            seedCategory(categories, "Electrician", "electrician", "Electrical repairs, installation and maintenance", "⚡");
            seedCategory(categories, "Carpenter", "carpenter", "Furniture repair, woodwork and fittings", "🪚");
            seedCategory(categories, "Painter", "painter", "Interior and exterior painting services", "🎨");
            seedCategory(categories, "Mechanic", "mechanic", "Two-wheeler and car repair services", "🔧");
            seedCategory(categories, "Plumber", "plumber", "Leaks, fittings and plumbing repairs", "🚿");
            seedCategory(categories, "Cleaning", "cleaning", "Home deep cleaning and maintenance", "✨");
            System.out.println("HomeServices administrator bootstrap checked for: " + adminEmail);
        };
    }
    private void seedCategory(ServiceCategoryRepository repo, String name, String slug, String description, String icon) {
        if (repo.findBySlug(slug).isEmpty()) repo.save(new ServiceCategory(name, slug, description, icon));
    }
}
