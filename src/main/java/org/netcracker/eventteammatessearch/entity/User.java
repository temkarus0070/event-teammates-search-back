package org.netcracker.eventteammatessearch.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String firstName;

    @NonNull
    private String lastName;

    @NonNull
    private String email;

    private String phone;

    @NonNull
    private String login;

    @NonNull
    private String password;

    @NonNull
    private String pictureUrl;

    @NonNull
    private LocalDate registrationDate;

    private boolean isAdmin;

    private boolean prefersOfflineEvents;

    private boolean isBlocked;

    @OneToMany(mappedBy = "user1")
    private Set<Relationship> users1;

    @OneToMany(mappedBy = "user2")
    private Set<Relationship> users2;

    @OneToMany(mappedBy = "user")
    private Set<PhoneToken> tokens;

    @OneToMany(mappedBy = "user")
    private Set<ChatUser> chats;

    @OneToMany(mappedBy = "user")
    private Set<EventAttendance> eventAttendances;

    @OneToMany(mappedBy = "owner")
    private Set<Event> createdEvents;

    @ManyToMany
    private Set<Event> invitations;
}
