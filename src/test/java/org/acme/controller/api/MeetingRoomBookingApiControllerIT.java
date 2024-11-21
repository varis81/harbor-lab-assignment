package org.acme.controller.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MeetingRoomBookingApiControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCreateBooking() throws Exception {
        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/booking")
                        .content("""
                                {
                                  "userEmail": "aris@test.com",
                                  "roomName": "Athens",
                                  "date": "2028-11-23",
                                  "startHour": 10,
                                  "startMinute": 30,
                                  "endHour": 11,
                                  "endMinute": 30,
                                  "zoneId": "Europe/Zurich"
                                }""")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
    }

    @Test
    void testCreateBookingAndExpect400ForTime() throws Exception {
        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/booking")
                        .content("""
                                {
                                  "userEmail": "aris@test.com",
                                  "roomName": "Athens",
                                  "date": "2028-11-23",
                                  "startHour": 12,
                                  "startMinute": 30,
                                  "endHour": 11,
                                  "endMinute": 30,
                                  "zoneId": "Europe/Zurich"
                                }""")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Start time is after end time"));
    }
}
