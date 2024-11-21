package org.acme.service;

import org.acme.dto.UserDto;
import org.acme.exception.UserExistsException;
import org.acme.model.User;
import org.acme.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserServiceI {

    @Autowired
    private UserRepository userRepository;

    public UserDto createUser(String name, String email) {
        if (userRepository.findByEmail(email) != null)
            throw new UserExistsException(String.format("A user with an email %s already exists", email));

        User user = new User(name, email);
        return userRepository.save(user).toDto();
    }

    public UserDto findUserByEmail(String email) {
        return userRepository.findByEmail(email).toDto();
    }

}
