package org.acme.service;

import org.acme.dto.UserDto;

public interface UserServiceI {

    UserDto createUser(String name, String email);
    UserDto findUserByEmail(String email);
}
