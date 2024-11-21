package org.acme.controller.admin;

import org.acme.dto.RoomDto;
import org.acme.exception.RoomExistsException;
import org.acme.service.RoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RoomAdminControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    @Test
    void testCreateRoom() throws Exception {
        RoomDto room = new RoomDto("Helsinki");
        when(roomService.createRoom("Helsinki")).thenReturn(room);

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/v1/room")
                        .param("name", "Helsinki")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());

        verify(roomService, times(1)).createRoom("Helsinki");
    }

    @Test
    void testBadRequestReturnedWhenRoomAlreadyExists() throws Exception {
        when(roomService.createRoom("Helsinki")).thenThrow(new RoomExistsException("RoomExists"));

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/v1/room")
                        .param("name", "Helsinki")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("RoomExists"));
    }

}
