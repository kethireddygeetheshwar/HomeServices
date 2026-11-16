package com.homeservices.api;

import com.homeservices.entity.AppUser;
import com.homeservices.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import com.homeservices.exception.ResourceNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingApiController {
    private final BookingService bookings;
    public BookingApiController(BookingService bookings) { this.bookings = bookings; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse create(@AuthenticationPrincipal AppUser user, @Valid @RequestBody CreateBookingRequest request) {
        var booking = bookings.createBooking(user, request.serviceId(), request.slotId(), request.notes());
        return new BookingResponse(booking.getId(), booking.getStatus().name(), booking.getCreatedAt());
    }

    @PatchMapping("/{id}/cancel")
    public MessageResponse cancel(@PathVariable Long id, @AuthenticationPrincipal AppUser user) {
        bookings.cancelByCustomer(id, user);
        return new MessageResponse("Booking cancelled.");
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFound(ResourceNotFoundException ex) { return new ErrorResponse(ex.getMessage()); }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse badRequest(IllegalArgumentException ex) { return new ErrorResponse(ex.getMessage()); }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse conflict(IllegalStateException ex) { return new ErrorResponse(ex.getMessage()); }

    public record CreateBookingRequest(@NotNull @Positive Long serviceId,
                                       @NotNull @Positive Long slotId,
                                       @Size(max = 1000) String notes) { }
    public record BookingResponse(Long id, String status, LocalDateTime createdAt) { }
    public record MessageResponse(String message) { }
    public record ErrorResponse(String error) { }
}
