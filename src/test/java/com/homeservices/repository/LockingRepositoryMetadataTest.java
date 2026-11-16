package com.homeservices.repository;

import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.Lock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LockingRepositoryMetadataTest {
    @Test
    void bookingLifecycleQueriesUsePessimisticWriteLocks() throws Exception {
        Lock customerLock = BookingRepository.class
                .getMethod("findByIdAndCustomer_IdForUpdate", Long.class, Long.class)
                .getAnnotation(Lock.class);
        Lock providerLock = BookingRepository.class
                .getMethod("findByIdAndProvider_IdForUpdate", Long.class, Long.class)
                .getAnnotation(Lock.class);
        assertNotNull(customerLock);
        assertNotNull(providerLock);
        assertEquals(LockModeType.PESSIMISTIC_WRITE, customerLock.value());
        assertEquals(LockModeType.PESSIMISTIC_WRITE, providerLock.value());
    }

    @Test
    void availabilityAndProviderSchedulingQueriesUsePessimisticWriteLocks() throws Exception {
        Lock slotLock = AvailabilitySlotRepository.class
                .getMethod("findByIdForUpdate", Long.class).getAnnotation(Lock.class);
        Lock overlapLock = AvailabilitySlotRepository.class
                .getMethod("findOverlappingForUpdate", Long.class, java.time.LocalDate.class,
                        java.time.LocalTime.class, java.time.LocalTime.class).getAnnotation(Lock.class);
        Lock providerLock = ProviderProfileRepository.class
                .getMethod("findByIdForUpdate", Long.class).getAnnotation(Lock.class);
        assertNotNull(slotLock);
        assertNotNull(overlapLock);
        assertNotNull(providerLock);
        assertEquals(LockModeType.PESSIMISTIC_WRITE, slotLock.value());
        assertEquals(LockModeType.PESSIMISTIC_WRITE, overlapLock.value());
        assertEquals(LockModeType.PESSIMISTIC_WRITE, providerLock.value());
    }
}
