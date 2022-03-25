package org.netcracker.eventteammatessearch.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@Table(name = "app_users")
public class User {
    @ElementCollection
    private List<GrantedAuthority> authorities;

    @Id
    @Column(unique = true, name = "login")
    private String login;

    @NonNull
    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    @NonNull
    @JsonIgnore
    private String password;

    private String pictureUrl;

    @NonNull
    private LocalDate registrationDate;

    public User(String login, @NonNull String password, List<GrantedAuthority> authorities) {
        this.login = login;
        this.password = password;
        this.authorities = authorities;
    }

    public User(String login) {
        this.login = login;
    }

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
    @JsonIgnore
    private Set<Event> createdEvents;

    @ManyToMany
    @ToString.Exclude
    private Set<Event> invitations;


    private boolean isCommercialUser;

    private String organizationName;

    private String description;



}
