package com.homeservices.service;

import com.homeservices.entity.*;
import com.homeservices.enums.ProviderStatus;
import com.homeservices.exception.ResourceNotFoundException;
import com.homeservices.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ProviderService {
    private final ProviderProfileRepository providers;
    private final ServiceCategoryRepository categories;
    private final ServiceOfferingRepository offerings;
    private final AvailabilitySlotRepository slots;

    public ProviderService(ProviderProfileRepository providers, ServiceCategoryRepository categories,
                           ServiceOfferingRepository offerings, AvailabilitySlotRepository slots) {
        this.providers = providers;
        this.categories = categories;
        this.offerings = offerings;
        this.slots = slots;
    }

    public ProviderProfile profileFor(AppUser user) {
        return providers.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found."));
    }

    @Transactional
    public ServiceOffering addService(AppUser user, Long categoryId, String title, String description,
                                      BigDecimal price, Integer durationMinutes) {
        ProviderProfile provider = profileFor(user);
        requireApproved(provider);

        if (title == null || title.isBlank() || title.trim().length() > 120) {
            throw new IllegalArgumentException("Service title is required and must be 120 characters or fewer.");
        }
        if (description != null && description.length() > 4000) {
            throw new IllegalArgumentException("Service description must be 4,000 characters or fewer.");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0 || price.scale() > 2
                || price.compareTo(new BigDecimal("99999999.99")) > 0) {
            throw new IllegalArgumentException("Enter a positive service price with no more than two decimal places (maximum ₹99,999,999.99).");
        }
        int duration = durationMinutes == null ? 60 : durationMinutes;
        if (duration < 15 || duration > 480) {
            throw new IllegalArgumentException("Service duration must be between 15 and 480 minutes.");
        }
        ServiceCategory category = categories.findByIdAndActiveTrue(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Choose an active service category."));

        ServiceOffering service = new ServiceOffering();
        service.setProvider(provider);
        service.setCategory(category);
        service.setTitle(title.trim());
        service.setDescription(description == null || description.isBlank() ? null : description.trim());
        service.setPrice(price);
        service.setDurationMinutes(duration);
        return offerings.save(service);
    }

    @Transactional
    public AvailabilitySlot addSlot(AppUser user, LocalDate date, LocalTime start, LocalTime end) {
        ProviderProfile initialProfile = profileFor(user);
        ProviderProfile provider = providers.findByIdForUpdate(initialProfile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found."));
        requireApproved(provider);

        if (date == null || date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Choose today or a future date.");
        }
        if (start == null || end == null || !end.isAfter(start)) {
            throw new IllegalArgumentException("End time must be later than start time.");
        }
        long minutes = Duration.between(start, end).toMinutes();
        if (minutes < 15 || minutes > 720) {
            throw new IllegalArgumentException("An availability slot must be between 15 minutes and 12 hours.");
        }
        if (date.equals(LocalDate.now()) && !start.isAfter(LocalTime.now())) {
            throw new IllegalArgumentException("Choose a future time.");
        }
        // Locking the provider row serializes slot creation for that provider. The
        // overlap query plus the unique key also protect normal duplicate inserts.
        if (!slots.findOverlappingForUpdate(provider.getId(), date, start, end).isEmpty()) {
            throw new IllegalArgumentException("This time overlaps an existing availability slot.");
        }
        AvailabilitySlot slot = new AvailabilitySlot();
        slot.setProvider(provider);
        slot.setSlotDate(date);
        slot.setStartTime(start);
        slot.setEndTime(end);
        slot.setAvailable(true);
        return slots.save(slot);
    }

    @Transactional
    public void setSlotAvailable(AppUser user, Long slotId, boolean makeAvailable) {
        ProviderProfile provider = profileFor(user);
        requireApproved(provider);
        AvailabilitySlot slot = slots.findByIdForUpdate(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability slot not found."));
        if (!slot.getProvider().getId().equals(provider.getId())) {
            throw new IllegalArgumentException("You cannot change another provider's availability.");
        }
        if (slot.getSlotDate().isBefore(LocalDate.now())
                || (makeAvailable && slot.getSlotDate().equals(LocalDate.now()) && !slot.getStartTime().isAfter(LocalTime.now()))) {
            throw new IllegalStateException("Past availability cannot be changed or reopened.");
        }
        if (!slot.isAvailable() && !slot.isBlocked()) {
            throw new IllegalStateException("This slot is already booked and cannot be changed.");
        }
        slot.setBlocked(!makeAvailable);
        slot.setAvailable(makeAvailable);
        slots.save(slot);
    }

    public List<ServiceOffering> getProviderServices(AppUser user) {
        ProviderProfile provider = profileFor(user);
        return offerings.findByProvider_IdAndActiveTrueOrderByCreatedAtDesc(provider.getId());
    }

    public List<AvailabilitySlot> getUpcomingSlots(AppUser user) {
        ProviderProfile provider = profileFor(user);
        return slots.findByProvider_IdAndSlotDateGreaterThanEqualOrderBySlotDateAscStartTimeAsc(provider.getId(), LocalDate.now());
    }

    public List<AvailabilitySlot> getAvailableSlots(Long providerId, LocalDate date) {
        return getAvailableSlots(providerId, date, null);
    }

    /**
     * Returns only slots that can accommodate the requested service duration.
     * The no-duration overload is retained for callers that need the provider's
     * complete availability calendar (for example, the generic availability API).
     */
    public List<AvailabilitySlot> getAvailableSlots(Long providerId, LocalDate date, Integer minimumDurationMinutes) {
        if (providerId == null || date == null) {
            throw new IllegalArgumentException("Provider and appointment date are required.");
        }
        if (minimumDurationMinutes != null && minimumDurationMinutes < 1) {
            throw new IllegalArgumentException("Minimum slot duration must be a positive number of minutes.");
        }
        ProviderProfile provider = providers.findById(providerId)
                .filter(profile -> profile.getStatus() == ProviderStatus.APPROVED)
                .orElseThrow(() -> new ResourceNotFoundException("Approved service provider not found."));
        if (date.isBefore(LocalDate.now())) {
            return List.of();
        }
        List<AvailabilitySlot> availableSlots = slots.findByProvider_IdAndSlotDateAndAvailableTrueOrderByStartTimeAsc(provider.getId(), date);
        return availableSlots.stream()
                .filter(slot -> !slot.isBlocked())
                .filter(slot -> date.isAfter(LocalDate.now()) || slot.getStartTime().isAfter(LocalTime.now()))
                .filter(slot -> minimumDurationMinutes == null
                        || Duration.between(slot.getStartTime(), slot.getEndTime()).toMinutes() >= minimumDurationMinutes)
                .toList();
    }

    private void requireApproved(ProviderProfile provider) {
        if (provider.getStatus() != ProviderStatus.APPROVED) {
            throw new IllegalStateException("Your provider account must be approved before using this feature.");
        }
    }
}
