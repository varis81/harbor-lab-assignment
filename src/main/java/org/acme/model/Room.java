package org.acme.model;

import jakarta.persistence.*;
import lombok.*;
import org.acme.dto.RoomDto;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    public Room(String name) {
        this.name = name;
    }

    public RoomDto toDto() {
        return new RoomDto(this.name);
    }
}