package org.acme.service;

import org.acme.dto.RoomDto;
import org.acme.exception.RoomExistsException;
import org.acme.model.Room;
import org.acme.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


@SpringBootTest // Boots up the entire application context
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // Uses an in-memory database
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Resets DB after each test
public class RoomServiceIT {

    @Autowired
    private RoomServiceI roomService;

    @Autowired
    private RoomRepository roomRepository;

    @Test
    void testCreateRoom() {
        // when
        RoomDto room = roomService.createRoom("Helsinki");

        // then
        Room retrievedRoom = roomRepository.findRoomByName(room.getName());

        assertThat(room.getName()).isNotNull();
        assertThat(room.getName()).isEqualTo(retrievedRoom.getName());
    }

    @Test
    void testDuplicateRoomNameThrowsException() {
        // when
        roomService.createRoom("Helsinki");

        // then
        assertThatThrownBy(() -> roomService.createRoom("Helsinki"))
                .isInstanceOf(RoomExistsException.class)
                .hasMessage("A room named Helsinki already exists");
    }

}
