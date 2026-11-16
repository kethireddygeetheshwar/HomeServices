package com.homeservices.service;

import com.homeservices.entity.*;
import com.homeservices.enums.ProviderStatus;
import com.homeservices.enums.Role;
import com.homeservices.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProviderServiceTest {
    @Mock ProviderProfileRepository providers;
    @Mock ServiceCategoryRepository categories;
    @Mock ServiceOfferingRepository offerings;
    @Mock AvailabilitySlotRepository slots;
    @InjectMocks ProviderService service;

    private AppUser providerUser;
    private ProviderProfile provider;

    @BeforeEach
    void setUp() {
        providerUser = new AppUser();
        providerUser.setId(7L);
        providerUser.setRole(Role.PROVIDER);
        provider = new ProviderProfile();
        provider.setId(8L);
        provider.setStatus(ProviderStatus.APPROVED);
    }

    @Test
    void pendingProviderCannotPublishService() {
        provider.setStatus(ProviderStatus.PENDING);
        when(providers.findByUser_Id(7L)).thenReturn(Optional.of(provider));
        assertThrows(IllegalStateException.class,
                () -> service.addService(providerUser, 1L, "Wiring repair", null, new java.math.BigDecimal("500"), 60));
        verifyNoInteractions(categories, offerings);
    }

    @Test
    void blankServiceTitleIsRejected() {
        when(providers.findByUser_Id(7L)).thenReturn(Optional.of(provider));
        assertThrows(IllegalArgumentException.class,
                () -> service.addService(providerUser, 1L, "   ", null, new java.math.BigDecimal("500"), 60));
        verifyNoInteractions(categories, offerings);
    }

    @Test
    void inactiveCategoryIsRejected() {
        when(providers.findByUser_Id(7L)).thenReturn(Optional.of(provider));
        when(categories.findByIdAndActiveTrue(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> service.addService(providerUser, 1L, "Wiring repair", null, new java.math.BigDecimal("500"), 60));
        verify(offerings, never()).save(any());
    }

    @Test
    void slotCreationLocksProviderBeforeCheckingOverlap() {
        LocalDate date = LocalDate.now().plusDays(1);
        when(providers.findByUser_Id(7L)).thenReturn(Optional.of(provider));
        when(providers.findByIdForUpdate(8L)).thenReturn(Optional.of(provider));
        when(slots.findOverlappingForUpdate(8L, date, LocalTime.of(10, 0), LocalTime.of(11, 0))).thenReturn(java.util.List.of());
        when(slots.save(any(AvailabilitySlot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AvailabilitySlot slot = service.addSlot(providerUser, date, LocalTime.of(10, 0), LocalTime.of(11, 0));

        assertTrue(slot.isAvailable());
        InOrder order = inOrder(providers, slots);
        order.verify(providers).findByUser_Id(7L);
        order.verify(providers).findByIdForUpdate(8L);
        order.verify(slots).findOverlappingForUpdate(8L, date, LocalTime.of(10, 0), LocalTime.of(11, 0));
        order.verify(slots).save(any(AvailabilitySlot.class));
    }


    @Test
    void onlyReturnsSlotsLongEnoughForRequestedServiceDuration() {
        LocalDate date = LocalDate.now().plusDays(1);
        AvailabilitySlot shortSlot = new AvailabilitySlot();
        shortSlot.setId(10L);
        shortSlot.setProvider(provider);
        shortSlot.setSlotDate(date);
        shortSlot.setStartTime(LocalTime.of(10, 0));
        shortSlot.setEndTime(LocalTime.of(11, 0)); // 60 minutes
        shortSlot.setAvailable(true);

        AvailabilitySlot longSlot = new AvailabilitySlot();
        longSlot.setId(11L);
        longSlot.setProvider(provider);
        longSlot.setSlotDate(date);
        longSlot.setStartTime(LocalTime.of(11, 0));
        longSlot.setEndTime(LocalTime.of(12, 15)); // 75 minutes
        longSlot.setAvailable(true);

        when(providers.findById(8L)).thenReturn(Optional.of(provider));
        when(slots.findByProvider_IdAndSlotDateAndAvailableTrueOrderByStartTimeAsc(8L, date))
                .thenReturn(java.util.List.of(shortSlot, longSlot));

        var result = service.getAvailableSlots(8L, date, 62);

        assertEquals(java.util.List.of(longSlot), result);
    }

    @Test
    void overlappingSlotIsRejected() {
        LocalDate date = LocalDate.now().plusDays(1);
        when(providers.findByUser_Id(7L)).thenReturn(Optional.of(provider));
        when(providers.findByIdForUpdate(8L)).thenReturn(Optional.of(provider));
        when(slots.findOverlappingForUpdate(8L, date, LocalTime.of(10, 0), LocalTime.of(11, 0))).thenReturn(java.util.List.of(new AvailabilitySlot()));
        assertThrows(IllegalArgumentException.class,
                () -> service.addSlot(providerUser, date, LocalTime.of(10, 0), LocalTime.of(11, 0)));
        verify(slots, never()).save(any());
    }
}
