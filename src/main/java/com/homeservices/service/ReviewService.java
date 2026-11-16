package com.homeservices.service;

import com.homeservices.entity.Review;
import com.homeservices.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviews;
    public ReviewService(ReviewRepository reviews) { this.reviews = reviews; }
    public List<Review> forProvider(Long providerId) {
        return reviews.findByBooking_Provider_IdOrderByCreatedAtDesc(providerId);
    }
    public boolean hasReview(Long bookingId) {
        return reviews.existsByBooking_Id(bookingId);
    }
    public double averageForProvider(Long providerId) {
        return reviews.findByBooking_Provider_IdOrderByCreatedAtDesc(providerId).stream()
                .mapToInt(Review::getRating).average().orElse(0.0);
    }
}
