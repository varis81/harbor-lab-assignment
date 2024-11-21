package org.acme.service;

import org.acme.dto.BookingDto;
import org.acme.dto.RoomDto;
import org.acme.dto.UserDto;
import org.acme.exception.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


@SpringBootTest // Boots up the entire application context
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // Uses an in-memory database
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Resets DB after each test
public class MeetingRoomBookingServiceIT {

    @Autowired
    private MeetingRoomBookingService meetingRoomBookingService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;

    @Test
    void testCreateBookingForRoom() {
        // when
        RoomDto room = roomService.createRoom("Helsinki");
        UserDto user = userService.createUser("Aris", UUID.randomUUID().toString().replace("-", "")+"@test.com");

        // then
        LocalDate bookingDate = LocalDate.now().plusDays(3);
        BookingDto booking = meetingRoomBookingService.createBooking(
                user.getEmail(),
                room.getName(),
                bookingDate,
                LocalTime.of(12, 0),
                LocalTime.of(13, 0),
                "Europe/Zurich"
        );

        assertThat(booking.getRoom().getName()).isEqualTo(room.getName());
        assertThat(booking.getUser().getEmail()).isEqualTo(user.getEmail());
        assertThat(booking.getTimeZone()).isEqualTo("Europe/Zurich");
        assertThat(booking.getStartTime()).isEqualTo(LocalDateTime.of(bookingDate, LocalTime.of(12, 0)));
        assertThat(booking.getEndTime()).isEqualTo(LocalDateTime.of(bookingDate, LocalTime.of(13, 0)));
    }

    @Test
    void testCreateOverlappingBookingThrowsException() {
        // given
        RoomDto room = roomService.createRoom("Helsinki");
        UserDto user = userService.createUser("Aris", UUID.randomUUID().toString().replace("-", "")+"@test.com");

        LocalDate bookingDate = LocalDate.now().plusDays(3);
        meetingRoomBookingService.createBooking(
                user.getEmail(),
                room.getName(),
                bookingDate,
                LocalTime.of(12, 0),
                LocalTime.of(13, 0),
                "Europe/Zurich"
        );

        // when
        assertThatThrownBy(() ->
                meetingRoomBookingService.createBooking(
                    user.getEmail(),
                    room.getName(),
                    bookingDate,
                    LocalTime.of(12, 0),
                    LocalTime.of(13, 0),
                    "Europe/Zurich"
        ))
                .isInstanceOf(BookingConflictException.class)
                .hasMessage("Room Helsinki is unavailable for the desired timeframe");;
    }

    @Test
    void testCreateOverlappingBookingOnDifferentTimezonesThrowsException() {
        // given
        RoomDto room = roomService.createRoom("Helsinki");
        UserDto user = userService.createUser("Aris", UUID.randomUUID().toString().replace("-", "")+"@test.com");

        LocalDate bookingDate = LocalDate.now().plusDays(3);
        meetingRoomBookingService.createBooking(
                user.getEmail(),
                room.getName(),
                bookingDate,
                LocalTime.of(12, 0),
                LocalTime.of(13, 0),
                "Europe/Zurich"
        );

        // when
        assertThatThrownBy(() ->
                meetingRoomBookingService.createBooking(
                        user.getEmail(),
                        room.getName(),
                        bookingDate,
                        LocalTime.of(13, 0),
                        LocalTime.of(14, 0),
                        "Europe/Athens"
                ))
                .isInstanceOf(BookingConflictException.class)
                .hasMessage("Room Helsinki is unavailable for the desired timeframe");;
    }

    @Test
    void testCreateNoMultiplesOf60MinutesBookingsThrowsException() {
        // given
        RoomDto room = roomService.createRoom("Helsinki");
        UserDto user = userService.createUser("Aris", UUID.randomUUID().toString().replace("-", "")+"@test.com");

        LocalDate bookingDate = LocalDate.now().plusDays(3);

        // when
        assertThatThrownBy(() ->
                meetingRoomBookingService.createBooking(
                        user.getEmail(),
                        room.getName(),
                        bookingDate,
                        LocalTime.of(13, 0),
                        LocalTime.of(14, 52),
                        "Europe/Athens"
                ))
                .isInstanceOf(BookingTimesNotValidException.class)
                .hasMessage("Meeting duration needs to be multiples of 1 hour");;
    }

    @Test
    void testCreateInvalidZoneIdThrowsException() {
        // given
        RoomDto room = roomService.createRoom("Helsinki");
        UserDto user = userService.createUser("Aris", UUID.randomUUID().toString().replace("-", "")+"@test.com");

        LocalDate bookingDate = LocalDate.now().plusDays(3);

        // when
        assertThatThrownBy(() ->
                meetingRoomBookingService.createBooking(
                        user.getEmail(),
                        room.getName(),
                        bookingDate,
                        LocalTime.of(13, 0),
                        LocalTime.of(14, 00),
                        "Eu/ens"
                ))
                .isInstanceOf(ZoneIdInvalidException.class)
                .hasMessage("ZoneId: Eu/ens is not a valid ZoneId");;
    }

    @Test
    void testCreateBookingIsInThePastThrowsException() {
        // given
        RoomDto room = roomService.createRoom("Helsinki");
        UserDto user = userService.createUser("Aris", UUID.randomUUID().toString().replace("-", "")+"@test.com");

        LocalDate bookingDate = LocalDate.now().minusDays(3);

        // when
        assertThatThrownBy(() ->
                meetingRoomBookingService.createBooking(
                        user.getEmail(),
                        room.getName(),
                        bookingDate,
                        LocalTime.of(13, 0),
                        LocalTime.of(14, 00),
                        "Europe/Athens"
                ))
                .isInstanceOf(BookingTimesNotValidException.class)
                .hasMessage("Start time is in the past");;
    }

    @Test
    void testCreateBookingStartIsAfterEndThrowsException() {
        // given
        RoomDto room = roomService.createRoom("Helsinki");
        UserDto user = userService.createUser("Aris", UUID.randomUUID().toString().replace("-", "")+"@test.com");

        LocalDate bookingDate = LocalDate.now().minusDays(3);

        // when
        assertThatThrownBy(() ->
                meetingRoomBookingService.createBooking(
                        user.getEmail(),
                        room.getName(),
                        bookingDate,
                        LocalTime.of(15, 0),
                        LocalTime.of(14, 0),
                        "Europe/Athens"
                ))
                .isInstanceOf(BookingTimesNotValidException.class)
                .hasMessage("Start time is after end time");;
    }

    @Test
    void testCreateBookingRoomDoesNotExistThrowsException() {
        // given
        UserDto user = userService.createUser("Aris", UUID.randomUUID().toString().replace("-", "")+"@test.com");
        LocalDate bookingDate = LocalDate.now().plusDays(3);

        // when
        assertThatThrownBy(() ->
                meetingRoomBookingService.createBooking(
                        user.getEmail(),
                        "blabla",
                        bookingDate,
                        LocalTime.of(14, 0),
                        LocalTime.of(15, 0),
                        "Europe/Athens"
                ))
                .isInstanceOf(RoomNotFoundException.class)
                .hasMessage("Room with name blabla does not exist");;
    }

    @Test
    void testCreateBookingUserDoesNotExistThrowsException() {
        // given
        RoomDto room = roomService.createRoom("Helsinki");LocalDate bookingDate = LocalDate.now().plusDays(3);

        // when
        assertThatThrownBy(() ->
                meetingRoomBookingService.createBooking(
                        "tesdah@test.com",
                        room.getName(),
                        bookingDate,
                        LocalTime.of(14, 0),
                        LocalTime.of(15, 0),
                        "Europe/Athens"
                ))
                .isInstanceOf(UserDoesNotExistException.class)
                .hasMessage("User with email tesdah@test.com does not exist");;
    }

    @Test
    void testRetrieveBookingOfRoomOnDate() {
        // given
        RoomDto room = roomService.createRoom(UUID.randomUUID().toString().replace("-", ""));
        RoomDto newRoom = roomService.createRoom(UUID.randomUUID().toString().replace("-", ""));
        UserDto user = userService.createUser("Aris", UUID.randomUUID().toString().replace("-", "")+"@test.com");

        LocalDate bookingDate = LocalDate.now().plusDays(3);
        meetingRoomBookingService.createBooking(
                user.getEmail(),
                room.getName(),
                bookingDate,
                LocalTime.of(12, 0),
                LocalTime.of(13, 0),
                "Europe/Zurich"
        );

        meetingRoomBookingService.createBooking(
                user.getEmail(),
                room.getName(),
                bookingDate,
                LocalTime.of(14, 0),
                LocalTime.of(16, 0),
                "Europe/Zurich"
        );

        meetingRoomBookingService.createBooking(
                user.getEmail(),
                newRoom.getName(),
                bookingDate,
                LocalTime.of(14, 0),
                LocalTime.of(16, 0),
                "Europe/Zurich"
        );

        meetingRoomBookingService.createBooking(
                user.getEmail(),
                room.getName(),
                bookingDate.plusDays(4),
                LocalTime.of(14, 0),
                LocalTime.of(16, 0),
                "Europe/Zurich"
        );

        // when
        List<BookingDto> bookings = meetingRoomBookingService.findBookingsOfRoomOnDate(room.getName(), bookingDate);

        assertThat(bookings).hasSize(2);
    }

    @Test
    void testCancelationOfBooking() {
        // given
        RoomDto room = roomService.createRoom(UUID.randomUUID().toString().replace("-", ""));
        UserDto user = userService.createUser("Aris", UUID.randomUUID().toString().replace("-", "")+"@test.com");

        LocalDate bookingDate = LocalDate.now().plusDays(3);
        BookingDto booking = meetingRoomBookingService.createBooking(
                user.getEmail(),
                room.getName(),
                bookingDate,
                LocalTime.of(12, 0),
                LocalTime.of(13, 0),
                "Europe/Zurich"
        );

        // when
        boolean response = meetingRoomBookingService.cancelBooking(booking.getBookingId().toString());

        assertThat(response).isTrue();

        boolean responseNonExistent = meetingRoomBookingService.cancelBooking("21512212");

        assertThat(responseNonExistent).isFalse();
    }
}
