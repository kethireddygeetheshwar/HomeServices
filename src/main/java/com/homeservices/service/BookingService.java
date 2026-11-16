package com.homeservices.service;

import com.homeservices.entity.*;
import com.homeservices.enums.BookingStatus;
import com.homeservices.enums.ProviderStatus;
import com.homeservices.enums.Role;
import com.homeservices.exception.ResourceNotFoundException;
import com.homeservices.repository.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookings;
    private final ServiceOfferingRepository offerings;
    private final AvailabilitySlotRepository slots;
    private final ProviderProfileRepository providers;
    private final ReviewRepository reviews;

    public BookingService(BookingRepository bookings, ServiceOfferingRepository offerings,
                          AvailabilitySlotRepository slots, ProviderProfileRepository providers,
                          ReviewRepository reviews) {
        this.bookings = bookings;
        this.offerings = offerings;
        this.slots = slots;
        this.providers = providers;
        this.reviews = reviews;
    }

    @Transactional
    public Booking createBooking(AppUser customer, Long serviceId, Long slotId, String notes) {
        if (customer == null || customer.getRole() != Role.CUSTOMER) {
            throw new IllegalStateException("Only customer accounts can create bookings.");
        }
        if (serviceId == null || slotId == null) {
            throw new IllegalArgumentException("Choose a service and an available time slot.");
        }
        if (notes != null && notes.length() > 1000) {
            throw new IllegalArgumentException("Booking notes must be 1,000 characters or fewer.");
        }

        ServiceOffering service = offerings.findPublicById(serviceId, ProviderStatus.APPROVED)
                .orElseThrow(() -> new ResourceNotFoundException("The selected service is not available."));
        AvailabilitySlot slot = slots.findByIdForUpdate(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("The selected time slot does not exist."));

        if (!slot.isAvailable() || slot.isBlocked()) {
            throw new IllegalStateException("That time slot has just been booked or blocked. Please select another slot.");
        }
        if (!slot.getProvider().getId().equals(service.getProvider().getId())) {
            throw new IllegalArgumentException("The selected time slot does not belong to this service provider.");
        }
        if (service.getProvider().getStatus() != ProviderStatus.APPROVED || !service.isActive()) {
            throw new IllegalStateException("This service provider or service is not currently available.");
        }
        if (!service.getCategory().isActive()) {
            throw new IllegalStateException("This service category is no longer active.");
        }
        if (slot.getSlotDate().isBefore(LocalDate.now())
                || (slot.getSlotDate().equals(LocalDate.now()) && !slot.getStartTime().isAfter(LocalTime.now()))) {
            throw new IllegalArgumentException("The selected time slot is in the past.");
        }
        long slotMinutes = Duration.between(slot.getStartTime(), slot.getEndTime()).toMinutes();
        if (slotMinutes < service.getDurationMinutes()) {
            throw new IllegalArgumentException("This service needs at least " + service.getDurationMinutes()
                    + " minutes, but the selected slot is only " + slotMinutes
                    + " minutes. Please choose a longer availability slot.");
        }

        // The pessimistic slot lock is held until this transaction commits. Only one
        // customer can change an available slot to booked at a time.
        slot.setAvailable(false);
        slots.save(slot);

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setProvider(service.getProvider());
        booking.setService(service);
        booking.setSlot(slot);
        booking.setStatus(BookingStatus.PENDING);
        booking.setCustomerNotes(notes == null || notes.isBlank() ? null : notes.trim());
        return bookings.save(booking);
    }

    @Transactional
    public void cancelByCustomer(Long bookingId, AppUser customer) {
        Booking booking = bookings.findByIdAndCustomer_IdForUpdate(bookingId, customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only pending or confirmed bookings can be cancelled.");
        }
        AvailabilitySlot slot = slots.findByIdForUpdate(booking.getSlot().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Availability slot not found."));
        booking.setStatus(BookingStatus.CANCELLED);
        restoreSlotIfFuture(slot);
        bookings.save(booking);
        slots.save(slot);
    }

    @Transactional
    public void providerAccept(Long bookingId, AppUser providerUser) {
        Booking booking = providerBookingForUpdate(bookingId, providerUser);
        requireStatus(booking, BookingStatus.PENDING);
        booking.setStatus(BookingStatus.CONFIRMED);
    }

    @Transactional
    public void providerReject(Long bookingId, AppUser providerUser) {
        Booking booking = providerBookingForUpdate(bookingId, providerUser);
        requireStatus(booking, BookingStatus.PENDING);
        AvailabilitySlot slot = slots.findByIdForUpdate(booking.getSlot().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Availability slot not found."));
        booking.setStatus(BookingStatus.REJECTED);
        restoreSlotIfFuture(slot);
        slots.save(slot);
    }

    @Transactional
    public void providerStart(Long bookingId, AppUser providerUser) {
        Booking booking = providerBookingForUpdate(bookingId, providerUser);
        requireStatus(booking, BookingStatus.CONFIRMED);
        booking.setStatus(BookingStatus.IN_PROGRESS);
    }

    @Transactional
    public void providerComplete(Long bookingId, AppUser providerUser) {
        Booking booking = providerBookingForUpdate(bookingId, providerUser);
        if (booking.getStatus() != BookingStatus.CONFIRMED && booking.getStatus() != BookingStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only confirmed or in-progress bookings can be marked completed.");
        }
        booking.setStatus(BookingStatus.COMPLETED);
    }

    @Transactional
    public void addReview(Long bookingId, AppUser customer, Integer rating, String comment) {
        Booking booking = bookings.findByIdAndCustomer_IdForUpdate(bookingId, customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new IllegalStateException("You can review a booking only after it is completed.");
        }
        if (reviews.existsByBooking_Id(bookingId)) {
            throw new IllegalStateException("You have already reviewed this booking.");
        }
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Choose a rating between 1 and 5.");
        }
        if (comment != null && comment.length() > 1200) {
            throw new IllegalArgumentException("Review comments must be 1,200 characters or fewer.");
        }
        Review review = new Review();
        review.setBooking(booking);
        review.setRating(rating);
        review.setComment(comment == null || comment.isBlank() ? null : comment.trim());
        try {
            reviews.saveAndFlush(review);
        } catch (DataIntegrityViolationException duplicate) {
            throw new IllegalStateException("You have already reviewed this booking.");
        }
    }

    public List<Booking> customerBookings(AppUser customer) {
        return bookings.findByCustomer_IdOrderByCreatedAtDesc(customer.getId());
    }

    public List<Booking> providerBookings(AppUser providerUser) {
        ProviderProfile profile = providers.findByUser_Id(providerUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found."));
        return bookings.findByProvider_IdOrderByCreatedAtDesc(profile.getId());
    }

    public long countAllBookings() { return bookings.count(); }
    public long countPendingBookings() { return bookings.countByStatus(BookingStatus.PENDING); }

    private Booking providerBookingForUpdate(Long id, AppUser providerUser) {
        ProviderProfile provider = providers.findByUser_Id(providerUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found."));
        return bookings.findByIdAndProvider_IdForUpdate(id, provider.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
    }

    private void requireStatus(Booking booking, BookingStatus expected) {
        if (booking.getStatus() != expected) {
            throw new IllegalStateException("This booking is no longer " + expected.name().toLowerCase() + ".");
        }
    }

    private void restoreSlotIfFuture(AvailabilitySlot slot) {
        boolean isFuture = slot.getSlotDate().isAfter(LocalDate.now())
                || (slot.getSlotDate().equals(LocalDate.now()) && slot.getStartTime().isAfter(LocalTime.now()));
        slot.setAvailable(!slot.isBlocked() && isFuture);
    }
}
