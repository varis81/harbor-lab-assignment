package org.acme.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.acme.dto.UserDto;
import org.acme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/v1/user")
public class UserAdminController {

    @Autowired
    private UserService userService;

    @PostMapping
    @Operation(
            description = "Call this POST request to create a meeting room User"
    )
    public ResponseEntity<UserDto> createRoom(
            @Valid
            @RequestBody
            UserDto userCreationRequest
    ) {
        UserDto user = userService.createUser(userCreationRequest.getName(), userCreationRequest.getEmail());

        return ResponseEntity.ok(user);
    }

    @GetMapping
    @Operation(
            description = "Call this GET request to retrieve a meeting room User by email"
    )
    public ResponseEntity<UserDto> findUserByEmail(
            @RequestParam String email
    ) {
        UserDto user = userService.findUserByEmail(email);

        return ResponseEntity.ok(user);
    }
}