package org.netcracker.eventteammatessearch.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
    @ToString.Exclude
    private List<GrantedAuthority> authorities;


    @Id
    @Column(unique = true, name = "login")
    private String login;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String password;

    private String pictureUrl;


    private LocalDate registrationDate;


    public User(String login) {
        this.login = login;
    }

    public User(String login,  String password, List<GrantedAuthority> authorities) {
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
    @JsonBackReference
    private Set<ChatUser> chats;

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private Set<EventAttendance> eventAttendances;

    @OneToMany(mappedBy = "owner")
    @ToString.Exclude
    @JsonIgnore
    private Set<Event> createdEvents;

    @ManyToMany(mappedBy = "invitedGuests")
    @ToString.Exclude
    @JsonIgnore
    private Set<Event> invitations;

    @OneToOne(mappedBy = "user")
    private Survey surveyResult;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @JsonIgnore
    @ToString.Exclude
    private Set<PayingInfo> receipts;

    private boolean isCommercialUser;

    private boolean isCommercialUserCreated;

    private String organizationName;

    private String description;

    private boolean oauthUser;
    private String oauthService;
    private String oauthKey;


}
