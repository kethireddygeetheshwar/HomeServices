package com.homeservices.repository;

import com.homeservices.entity.AvailabilitySlot;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {
    List<AvailabilitySlot> findByProvider_IdAndSlotDateAndAvailableTrueOrderByStartTimeAsc(Long providerId, LocalDate date);
    List<AvailabilitySlot> findByProvider_IdAndSlotDateGreaterThanEqualOrderBySlotDateAscStartTimeAsc(Long providerId, LocalDate date);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from AvailabilitySlot s where s.id = :id")
    Optional<AvailabilitySlot> findByIdForUpdate(@Param("id") Long id);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from AvailabilitySlot s where s.provider.id = :providerId and s.slotDate = :date and s.startTime < :endTime and s.endTime > :startTime")
    List<AvailabilitySlot> findOverlappingForUpdate(@Param("providerId") Long providerId,
                                                    @Param("date") LocalDate date,
                                                    @Param("startTime") java.time.LocalTime startTime,
                                                    @Param("endTime") java.time.LocalTime endTime);
}
