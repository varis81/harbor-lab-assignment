package org.acme.service;

import org.acme.dto.BookingDto;
import org.acme.exception.*;
import org.acme.model.Booking;
import org.acme.model.Room;
import org.acme.model.User;
import org.acme.repository.BookingRepository;
import org.acme.repository.RoomRepository;
import org.acme.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MeetingRoomBookingService implements MeetingRoomBookingServiceI {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    public BookingDto createBooking(
            String userEmail,
            String roomName,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            String zoneId
    ) {
        validateDates(date, startTime, endTime, zoneId);

        Room room = roomRepository.findRoomByName(roomName);
        if (room == null)
            throw new RoomNotFoundException("Room with name " + roomName + " does not exist");

        User user = userRepository.findByEmail(userEmail);
        if (user == null)
            throw new UserDoesNotExistException("User with email " + userEmail + " does not exist");

        ZonedDateTime utcDateStartTime = getZonedDateTimeUTC(date, startTime, zoneId);
        ZonedDateTime utcDateEndTime = getZonedDateTimeUTC(date, endTime, zoneId);

        validateNoConflicts(room, utcDateStartTime, utcDateEndTime);

        // We save the booking on utc time so that we can have a common way of comparing times. We also save the zoneId so that we can convert back to the original time
        return bookingRepository.save(
                new Booking(user, room, utcDateStartTime.toLocalDateTime(), utcDateEndTime.toLocalDateTime(), zoneId)
        ).toDto();
    }

    private void validateNoConflicts(Room room, ZonedDateTime utcDateStartTime, ZonedDateTime utcDateEndTime) {
        List<Booking> bookings = bookingRepository.findBookingsForDate(room, utcDateStartTime.toLocalDateTime(), utcDateEndTime.toLocalDateTime());
        if (bookings.size() > 0)
            throw new BookingConflictException("Room " + room.getName() + " is unavailable for the desired timeframe");
    }

    private void validateDates(LocalDate date, LocalTime startTime, LocalTime endTime, String zone) {
        // this is to satisfy the constraint of the assignment that a room can be booked for at least 1 hour and multiples of 1 hour
        if (Duration.between(startTime, endTime).toMinutes() % 60 != 0) {
            throw new BookingTimesNotValidException("Meeting duration needs to be multiples of 1 hour");
        }

        ZonedDateTime utcDateStartTime = getZonedDateTimeUTC(date, startTime, zone);
        ZonedDateTime utcDateEndTime = getZonedDateTimeUTC(date, endTime, zone);

        if (utcDateStartTime.isAfter(utcDateEndTime))
            throw new BookingTimesNotValidException("Start time is after end time");
        if (utcDateStartTime.isBefore(ZonedDateTime.now(ZoneId.of("UTC"))))
            throw new BookingTimesNotValidException("Start time is in the past");
    }

    private ZonedDateTime getZonedDateTimeUTC(LocalDate date, LocalTime time, String zone) {
        // convert dates to a ZonedDateTime
        ZoneId zoneId;
        try {
            zoneId = ZoneId.of(zone);
        } catch (DateTimeException ex) {
            throw new ZoneIdInvalidException("ZoneId: " + zone + " is not a valid ZoneId");
        }

        // Combine LocalDate and LocalTime into LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.of(date, time);

        // Convert LocalDateTime to ZonedDateTime using the ZoneId
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);

        // Convert to UTC so that we can have a common point of reference
        return zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
    }

    public List<BookingDto> findBookingsOfRoomOnDate(String roomName, LocalDate date) {
       Room room = roomRepository.findRoomByName(roomName);
       if (room == null)
           throw new RoomNotFoundException("Room with name " + roomName + " does not exist");

       return bookingRepository.findBookingsForDate(room, date.atStartOfDay(), date.plusDays(1).atStartOfDay())
               .stream()
               .map(booking -> booking.toDto())
               .collect(Collectors.toList());
    }

    public boolean cancelBooking(String id) {
        if (bookingRepository.existsById(Long.parseLong(id))) {
            bookingRepository.deleteById(Long.parseLong(id));
            return true;
        }
        return false;
    }

}

