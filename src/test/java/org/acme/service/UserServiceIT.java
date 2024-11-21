package org.acme.service;

import org.acme.dto.UserDto;
import org.acme.exception.UserExistsException;
import org.acme.model.User;
import org.acme.repository.UserRepository;
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
public class UserServiceIT {

    @Autowired
    private UserServiceI userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testCreateRoom() {
        // when
        UserDto user = userService.createUser("Aris", "aris@test.com");

        // then
        User retrievedUser = userRepository.findByEmail(user.getEmail());

        assertThat(user.getName()).isNotNull();
        assertThat(user.getName()).isEqualTo(retrievedUser.getName());
        assertThat(user.getEmail()).isEqualTo(retrievedUser.getEmail());
    }

    @Test
    void testDuplicateUserEmailThrowsException() {
        // when
        userService.createUser("Aris", "aris@test.com");

        // then
        assertThatThrownBy(() -> userService.createUser("Aris", "aris@test.com"))
                .isInstanceOf(UserExistsException.class)
                .hasMessage("A user with an email aris@test.com already exists");
    }

}
