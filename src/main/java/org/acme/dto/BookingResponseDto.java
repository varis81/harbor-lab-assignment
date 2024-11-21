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
public class BookingResponseDto {

    private Long bookingId;

    private String userEmail;

    private String roomName;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String timeZone;

}
