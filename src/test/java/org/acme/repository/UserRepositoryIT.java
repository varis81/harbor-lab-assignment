package org.acme.repository;
import org.acme.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindById() {
        // given
        User user = new User("john_doe", "john.doe@example.com");

        // when
        User savedUser = userRepository.save(user);
        Optional<User> retrievedUser = userRepository.findById(savedUser.getId());

        // Assert
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getName()).isEqualTo(user.getName());
        assertThat(retrievedUser.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void testFindByEmail() {
        // Arrange
        User user = new User("jane_doe", "jane.doe@example.com");
        userRepository.save(user);

        // Act
        User retrievedUser = userRepository.findByEmail(user.getEmail());

        // Assert
        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getEmail()).isEqualTo(user.getEmail());
    }
}