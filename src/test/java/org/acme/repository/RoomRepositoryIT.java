package org.acme.repository;
import org.acme.model.Room;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RoomRepositoryIT {

    @Autowired
    private RoomRepository roomRepository;

    @Test
    void testSaveAndFindById() {
        // given
        Room room = new Room( "Helsinki");

        // when
        Room savedRoom = roomRepository.save(room);
        Optional<Room> retrievedUser = roomRepository.findById(savedRoom.getId());

        // Assert
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getName()).isEqualTo(room.getName());
    }

    @Test
    void testFindByName() {
        // Arrange
        Room room = new Room("Rome");
        roomRepository.save(room);

        // Act
        Room retrievedRoom = roomRepository.findRoomByName(room.getName());

        // Assert
        assertThat(retrievedRoom).isNotNull();
        assertThat(retrievedRoom.getName()).isEqualTo(room.getName());
    }
}