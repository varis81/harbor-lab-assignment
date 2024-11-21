package org.acme.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.acme.dto.BookingDto;
import org.acme.dto.BookingRequest;
import org.acme.dto.BookingResponseDto;
import org.acme.service.MeetingRoomBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/booking")
public class MeetingRoomBookingApiController {

    @Autowired
    private MeetingRoomBookingService bookingService;

    @PostMapping
    @Operation(
            description = "Call this POST request to create a booking for a room. Room name, user email and start and end times are required. Format for times are: 2024-12-25 12:00:00"
    )
    public ResponseEntity<BookingResponseDto> createBooking(
            @Valid @RequestBody BookingRequest bookingRequest
    ) {
        LocalTime startTime = getLocalTimeFromInput(bookingRequest.getStartHour(), bookingRequest.getStartMinute());
        LocalTime endTime = getLocalTimeFromInput(bookingRequest.getEndHour(), bookingRequest.getEndMinute());

        BookingDto booking = bookingService.createBooking(
                bookingRequest.getUserEmail(),
                bookingRequest.getRoomName(),
                bookingRequest.getDate(),
                startTime,
                endTime,
                bookingRequest.getZoneId()
        );

        return ResponseEntity.ok(booking.toBookingResponseDto());
    }

    @GetMapping
    @Operation(
            description = "Call this GET request to retrieve the bookings of a room. RoomName and date are required. Format for date is: 2024-12-25"
    )
    public ResponseEntity<List<BookingResponseDto>> retrieveBookingsForRoomOnDate(
            @RequestParam(required = true) String roomName,
            @RequestParam(required = true) LocalDate date
            ) {
        List<BookingResponseDto> bookings = bookingService.findBookingsOfRoomOnDate(roomName, date).stream()
                .map(booking -> booking.toBookingResponseDto())
                .collect(Collectors.toList());

        return ResponseEntity.ok(bookings);
    }

    @DeleteMapping("/{id}")
    @Operation(
            description = "Call this DELETE request to cancel the booking of a room. The id of the booking is required, retrievable through a get request"
    )
    public ResponseEntity<String> cancelABooking(
            @PathVariable String id
    ) {
        boolean result = bookingService.cancelBooking(id);
        String responseText = result ? "Booking has been cancelled" : "No booking with id " + id + " exists";

        return ResponseEntity.ok(responseText);
    }

    private LocalTime getLocalTimeFromInput(Integer hour, Integer minute) {
        try {
            return LocalTime.of(hour, minute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid hour and/or minute format");
        }
    }
}