package org.acme.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.acme.dto.UserDto;
import org.acme.exception.UserExistsException;
import org.acme.service.UserService;
import org.junit.jupiter.api.BeforeEach;
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
public class UserAdminControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();  // Jackson ObjectMapper to convert objects to JSON
    }

    @Test
    void testCreateUser() throws Exception {
        UserDto user = new UserDto("Aris", "aris@test.com");
        when(userService.createUser("Aris", "aris@test.com")).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/v1/user")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());

        verify(userService, times(1)).createUser("Aris", "aris@test.com");
    }

    @Test
    void testBadRequestReturnedWhenUserEmailAlreadyExists() throws Exception {
        UserDto user = new UserDto("Aris", "aris@test.com");
        when(userService.createUser("Aris", "aris@test.com")).thenThrow(new UserExistsException("UserExists"));

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/v1/user")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("UserExists"));
    }

    @Test
    void testBadRequestReturnedWhenEmailIsNotValid() throws Exception {
        UserDto user = new UserDto("Aris", "notAnEmail");

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/v1/user")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"email\":\"Invalid email format\"}"));
    }

}
