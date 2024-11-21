package org.acme.service;

import org.acme.dto.RoomDto;
import org.acme.exception.RoomExistsException;
import org.acme.model.Room;
import org.acme.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomService implements RoomServiceI {

    @Autowired
    private RoomRepository roomRepository;

    public RoomDto createRoom(String name) {
        if (roomRepository.findRoomByName(name) != null)
            throw new RoomExistsException(String.format("A room named %s already exists", name));
        Room room = new Room(name);
        return roomRepository.save(room).toDto();
    }

    public RoomDto findRoom(String name) {
        return roomRepository.findRoomByName(name).toDto();
    }
}
