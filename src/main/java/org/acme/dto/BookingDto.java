package org.acme.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

    private Long bookingId;

    private UserDto user;

    private RoomDto room;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String timeZone;

    public BookingResponseDto toBookingResponseDto() {
        return new BookingResponseDto(
                this.bookingId,
                this.user.getEmail(),
                this.room.getName(),
                this.startTime,
                this.endTime,
                this.timeZone
        );
    }
}
