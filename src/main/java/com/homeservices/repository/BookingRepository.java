package com.homeservices.repository;

import com.homeservices.entity.Booking;
import com.homeservices.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @EntityGraph(attributePaths = {"service", "service.category", "provider", "slot", "customer"})
    List<Booking> findByCustomer_IdOrderByCreatedAtDesc(Long customerId);
    @EntityGraph(attributePaths = {"service", "service.category", "provider", "slot", "customer"})
    List<Booking> findByProvider_IdOrderByCreatedAtDesc(Long providerId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Booking b where b.id = :id and b.customer.id = :customerId")
    Optional<Booking> findByIdAndCustomer_IdForUpdate(@Param("id") Long id, @Param("customerId") Long customerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Booking b where b.id = :id and b.provider.id = :providerId")
    Optional<Booking> findByIdAndProvider_IdForUpdate(@Param("id") Long id, @Param("providerId") Long providerId);
    long countByStatus(BookingStatus status);
    long countByProvider_Id(Long providerId);
}
