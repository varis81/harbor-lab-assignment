package org.acme.service;

import org.acme.dto.BookingDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface MeetingRoomBookingServiceI {

    BookingDto createBooking(String userEmail, String roomName, LocalDate date, LocalTime startTime, LocalTime endTime, String zoneId);
    List<BookingDto> findBookingsOfRoomOnDate(String roomName, LocalDate date);
    boolean cancelBooking(String id);
}
