package org.acme.model;

import jakarta.persistence.*;
import lombok.*;
import org.acme.dto.UserDto;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "room_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public UserDto toDto() {
        return new UserDto(this.name, this.email);
    }

}
