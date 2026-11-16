package com.homeservices.repository;

import com.homeservices.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByBooking_Id(Long bookingId);
    @EntityGraph(attributePaths = {"booking", "booking.customer"})
    List<Review> findByBooking_Provider_IdOrderByCreatedAtDesc(Long providerId);
    long count();
}
