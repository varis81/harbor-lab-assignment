package org.acme.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class BookingRequest {

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email format")
    private String userEmail;

    @NotBlank(message = "RoomName is mandatory")
    private String roomName;

    @NotNull(message = "Date is mandatory")
    private LocalDate date;

    @NotNull(message = "startHour is mandatory")
    private Integer startHour;

    @NotNull(message = "startMinute is mandatory")
    private Integer startMinute;

    @NotNull(message = "endHour is mandatory")
    private Integer endHour;

    @NotNull(message = "endMinute is mandatory")
    private Integer endMinute;

    @NotBlank(message = "zoneId is mandatory")
    private String zoneId = "UTC";
}
