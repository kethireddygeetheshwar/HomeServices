package com.homeservices.service;

import com.homeservices.entity.*;
import com.homeservices.enums.BookingStatus;
import com.homeservices.enums.ProviderStatus;
import com.homeservices.enums.Role;
import com.homeservices.exception.ResourceNotFoundException;
import com.homeservices.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock BookingRepository bookings;
    @Mock ServiceOfferingRepository offerings;
    @Mock AvailabilitySlotRepository slots;
    @Mock ProviderProfileRepository providers;
    @Mock ReviewRepository reviews;
    @InjectMocks BookingService service;

    private AppUser customer;
    private ProviderProfile provider;
    private ServiceOffering offering;
    private AvailabilitySlot slot;

    @BeforeEach
    void setUp() {
        customer = new AppUser();
        customer.setId(11L);
        customer.setRole(Role.CUSTOMER);

        provider = new ProviderProfile();
        provider.setId(21L);
        provider.setStatus(ProviderStatus.APPROVED);

        ServiceCategory category = new ServiceCategory("Electrician", "electrician", "Electrical", "⚡");
        offering = new ServiceOffering();
        offering.setId(31L);
        offering.setProvider(provider);
        offering.setCategory(category);
        offering.setActive(true);
        offering.setDurationMinutes(60);
        offering.setPrice(new BigDecimal("500.00"));

        slot = new AvailabilitySlot();
        slot.setId(41L);
        slot.setProvider(provider);
        slot.setSlotDate(LocalDate.now().plusDays(1));
        slot.setStartTime(LocalTime.of(10, 0));
        slot.setEndTime(LocalTime.of(11, 0));
        slot.setAvailable(true);
    }

    @Test
    void createsPendingBookingAndReservesSlot() {
        when(offerings.findPublicById(31L, ProviderStatus.APPROVED)).thenReturn(Optional.of(offering));
        when(slots.findByIdForUpdate(41L)).thenReturn(Optional.of(slot));
        when(bookings.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = service.createBooking(customer, 31L, 41L, "  Please call first.  ");

        assertEquals(BookingStatus.PENDING, result.getStatus());
        assertSame(customer, result.getCustomer());
        assertSame(provider, result.getProvider());
        assertFalse(slot.isAvailable());
        assertEquals("Please call first.", result.getCustomerNotes());
        verify(slots).save(slot);
        verify(bookings).save(result);
    }

    @Test
    void rejectsNonCustomerBeforeDatabaseAccess() {
        AppUser providerUser = new AppUser();
        providerUser.setRole(Role.PROVIDER);
        assertThrows(IllegalStateException.class, () -> service.createBooking(providerUser, 31L, 41L, null));
        verifyNoInteractions(offerings, slots, bookings);
    }

    @Test
    void rejectsAlreadyReservedOrBlockedSlot() {
        slot.setAvailable(false);
        when(offerings.findPublicById(31L, ProviderStatus.APPROVED)).thenReturn(Optional.of(offering));
        when(slots.findByIdForUpdate(41L)).thenReturn(Optional.of(slot));
        assertThrows(IllegalStateException.class, () -> service.createBooking(customer, 31L, 41L, null));
        verify(bookings, never()).save(any());
    }

    @Test
    void rejectsSlotOwnedByDifferentProvider() {
        ProviderProfile otherProvider = new ProviderProfile();
        otherProvider.setId(99L);
        slot.setProvider(otherProvider);
        when(offerings.findPublicById(31L, ProviderStatus.APPROVED)).thenReturn(Optional.of(offering));
        when(slots.findByIdForUpdate(41L)).thenReturn(Optional.of(slot));
        assertThrows(IllegalArgumentException.class, () -> service.createBooking(customer, 31L, 41L, null));
        verify(bookings, never()).save(any());
    }

    @Test
    void rejectsSlotShorterThanServiceDuration() {
        slot.setEndTime(LocalTime.of(10, 30));
        when(offerings.findPublicById(31L, ProviderStatus.APPROVED)).thenReturn(Optional.of(offering));
        when(slots.findByIdForUpdate(41L)).thenReturn(Optional.of(slot));
        assertThrows(IllegalArgumentException.class, () -> service.createBooking(customer, 31L, 41L, null));
        verify(bookings, never()).save(any());
    }

    @Test
    void rejectsNotesLongerThanDatabaseColumn() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createBooking(customer, 31L, 41L, "x".repeat(1001)));
        verifyNoInteractions(offerings, slots, bookings);
    }

    @Test
    void cancellationLocksBookingAndRestoresFutureSlot() {
        Booking booking = new Booking();
        booking.setId(51L);
        booking.setCustomer(customer);
        booking.setSlot(slot);
        booking.setStatus(BookingStatus.PENDING);
        slot.setAvailable(false);
        when(bookings.findByIdAndCustomer_IdForUpdate(51L, customer.getId())).thenReturn(Optional.of(booking));
        when(slots.findByIdForUpdate(41L)).thenReturn(Optional.of(slot));

        service.cancelByCustomer(51L, customer);

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertTrue(slot.isAvailable());
        verify(bookings).save(booking);
        verify(slots).save(slot);
    }

    @Test
    void cannotCancelCompletedBooking() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.COMPLETED);
        when(bookings.findByIdAndCustomer_IdForUpdate(51L, customer.getId())).thenReturn(Optional.of(booking));
        assertThrows(IllegalStateException.class, () -> service.cancelByCustomer(51L, customer));
        verify(slots, never()).findByIdForUpdate(anyLong());
    }

    @Test
    void reviewRequiresCompletedBooking() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.CONFIRMED);
        when(bookings.findByIdAndCustomer_IdForUpdate(51L, customer.getId())).thenReturn(Optional.of(booking));
        assertThrows(IllegalStateException.class, () -> service.addReview(51L, customer, 5, "Good"));
        verify(reviews, never()).saveAndFlush(any());
    }

    @Test
    void duplicateReviewIsRejectedBeforeInsert() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.COMPLETED);
        when(bookings.findByIdAndCustomer_IdForUpdate(51L, customer.getId())).thenReturn(Optional.of(booking));
        when(reviews.existsByBooking_Id(51L)).thenReturn(true);
        assertThrows(IllegalStateException.class, () -> service.addReview(51L, customer, 5, "Good"));
        verify(reviews, never()).saveAndFlush(any());
    }
}
