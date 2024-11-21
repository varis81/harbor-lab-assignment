package org.acme.model;

import jakarta.persistence.*;
import lombok.*;
import org.acme.dto.BookingDto;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Room room;

    private LocalDateTime utcStartTime;

    private LocalDateTime utcEndTime;

    private String timeZone;

    public Booking(User user, Room room, LocalDateTime utcStartTime, LocalDateTime utcEndTime, String timeZone) {
        this.user = user;
        this.room = room;
        this.utcStartTime = utcStartTime;
        this.utcEndTime = utcEndTime;
        this.timeZone = timeZone;
    }

    public BookingDto toDto() {
        // utc time needs to be converted to the users time zone and then to localtime so that it will be what the user initially booked
        LocalDateTime startTime = this.utcStartTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of(this.getTimeZone())).toLocalDateTime();
        LocalDateTime endTime = this.utcEndTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of(this.getTimeZone())).toLocalDateTime();

        return new BookingDto(this.id, this.user.toDto(), this.room.toDto(), startTime, endTime, this.timeZone);
    }
}
