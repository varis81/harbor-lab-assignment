package org.acme.repository;
import org.acme.model.Booking;
import org.acme.model.Room;
import org.acme.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookingRepositoryIT {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testCreateBooking() {
        // given
        Room room = new Room( "Helsinki");
        Room savedRoom = roomRepository.save(room);

        User user = new User("john_doe", "john.doe@example.com");
        User savedUser = userRepository.save(user);

        // when
        LocalDateTime now = LocalDateTime.now();
        Booking booking = new Booking(
                savedUser,
                savedRoom,
                now.plusDays(3),
                now.plusDays(3).plusHours(1),
                "Europe/Zurich"
        );
        Booking savedBooking = bookingRepository.save(booking);
        Booking retrievedBooking = bookingRepository.findById(savedBooking.getId()).get();

        // Assert
        assertThat(savedBooking.getId()).isEqualTo(retrievedBooking.getId());
    }

    @Test
    void testRetrieveBookings() {
        // given
        Room room = new Room( "Helsinki");
        Room savedRoom = roomRepository.save(room);

        User user = new User("john_doe", "john.doe@example.com");
        User savedUser = userRepository.save(user);

        // when
        LocalDateTime now = LocalDateTime.now();
        LocalDate dateNow = LocalDate.now();

        bookingRepository.save(new Booking(
                savedUser,
                savedRoom,
                now.plusDays(3),
                now.plusDays(3).plusHours(1),
                "Europe/Zurich"
        ));

        bookingRepository.save(new Booking(
                savedUser,
                savedRoom,
                now.plusDays(3).plusHours(2),
                now.plusDays(3).plusHours(3),
                "Europe/Zurich"
        ));

        bookingRepository.save(new Booking(
                savedUser,
                savedRoom,
                now.plusDays(30).plusHours(2),
                now.plusDays(30).plusHours(3),
                "Europe/Zurich"
        ));

        List<Booking> bookings = bookingRepository.findBookingsForDate(savedRoom, dateNow.plusDays(3).atStartOfDay(), dateNow.plusDays(4).atStartOfDay());

        // Assert
        assertThat(bookings.size()).isEqualTo(2);
    }

}