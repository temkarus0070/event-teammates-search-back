package org.netcracker.eventteammatessearch.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@Table(name = "app_users",
        uniqueConstraints = {@UniqueConstraint(name = "username_constraint", columnNames = {"login"})})
public class User {
    @Id
    private String login;

    @NonNull
    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    @NonNull
    private String password;

    private String pictureUrl;

    @NonNull
    private LocalDate registrationDate;

    private boolean isAdmin;

    private boolean prefersOfflineEvents;

    private boolean isBlocked;

    @Transient
    private double rating; // NOT PERSISTED AT this TABLE


    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private Set<PhoneToken> tokens;

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private Set<ChatUser> chats;

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private Set<EventAttendance> eventAttendances;

    @OneToMany(mappedBy = "owner")
    @ToString.Exclude
    private Set<Event> createdEvents;

    @ManyToMany
    @ToString.Exclude
    private Set<Event> invitations;


    private boolean isCommercialUser;

    private String organizationName;

    private String description;



}
