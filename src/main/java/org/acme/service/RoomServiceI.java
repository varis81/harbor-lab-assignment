package org.acme.service;

import org.acme.dto.RoomDto;

public interface RoomServiceI {

    RoomDto createRoom(String name);
    RoomDto findRoom(String name);
}
