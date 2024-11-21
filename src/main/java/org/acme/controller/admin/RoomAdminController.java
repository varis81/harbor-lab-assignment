package org.acme.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import org.acme.dto.RoomDto;
import org.acme.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/v1/room")
public class RoomAdminController {

    @Autowired
    private RoomService roomService;

    @PostMapping
    @Operation(
            description = "Call this POST request to create a meeting room"
    )
    public ResponseEntity<RoomDto> createRoom(
            @RequestParam String name
    ) {
        RoomDto room = roomService.createRoom(name);

        return ResponseEntity.ok(room);
    }

    @GetMapping
    @Operation(
            description = "Call this GET request to retrieve a meeting room by its name"
    )
    public ResponseEntity<RoomDto> findRoomByName(
            @RequestParam String name
    ) {
        RoomDto room = roomService.findRoom(name);

        return ResponseEntity.ok(room);
    }
}