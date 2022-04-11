package org.netcracker.eventteammatessearch.entity;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@NoArgsConstructor
@Table(name = "app_users")
public class User {
    @ElementCollection
    @JsonIgnore
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
    private String password;

    private String pictureUrl;

    @NonNull
    private LocalDate registrationDate;


    public User(String login) {
        this.login = login;
    }

    public User(String login, @NonNull String password, List<GrantedAuthority> authorities) {
        this.login = login;
        this.password = password;
        this.authorities = authorities;
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
    @JsonManagedReference

    private Set<ChatUser> chats;

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    @JsonIdentityReference(alwaysAsId = true)
    private Set<EventAttendance> eventAttendances;

    @OneToMany(mappedBy = "owner")
    @ToString.Exclude
    @JsonIgnore
    private Set<Event> createdEvents;

    @ManyToMany
    @ToString.Exclude
    private Set<Event> invitations;

    @OneToOne(mappedBy = "user")
    private Survey surveyResult;


    private boolean isCommercialUser;

    private String organizationName;

    private String description;



}
