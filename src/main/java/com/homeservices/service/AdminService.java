package com.homeservices.service;

import com.homeservices.entity.ProviderProfile;
import com.homeservices.enums.ProviderStatus;
import com.homeservices.exception.ResourceNotFoundException;
import com.homeservices.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AdminService {
    private final ProviderProfileRepository providers;
    private final AppUserRepository users;
    private final ServiceOfferingRepository offerings;
    private final BookingRepository bookings;
    private final ReviewRepository reviews;
    public AdminService(ProviderProfileRepository providers, AppUserRepository users,
                        ServiceOfferingRepository offerings, BookingRepository bookings, ReviewRepository reviews) {
        this.providers = providers; this.users = users; this.offerings = offerings; this.bookings = bookings; this.reviews = reviews;
    }
    public List<ProviderProfile> pendingProviders() { return providers.findByStatusOrderByCreatedAtDesc(ProviderStatus.PENDING); }
    public long customerCount() { return users.countByRole(com.homeservices.enums.Role.CUSTOMER); }
    public long providerCount() { return providers.count(); }
    public long pendingProviderCount() { return providers.countByStatus(ProviderStatus.PENDING); }
    public long serviceCount() { return offerings.countByActiveTrue(); }
    public long bookingCount() { return bookings.count(); }
    public long reviewCount() { return reviews.count(); }
    @Transactional public void updateProviderStatus(Long providerId, ProviderStatus status) {
        ProviderProfile provider = providers.findByIdForUpdate(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found."));
        if (provider.getStatus() != ProviderStatus.PENDING) {
            throw new IllegalStateException("Only pending provider applications can be reviewed.");
        }
        if (status != ProviderStatus.APPROVED && status != ProviderStatus.REJECTED) {
            throw new IllegalArgumentException("Choose approve or reject for this provider application.");
        }
        provider.setStatus(status);
        providers.save(provider);
    }
}
