package org.netcracker.eventteammatessearch.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_users")
@NamedEntityGraph(name = "user-graph",includeAllAttributes = true,attributeNodes =
        {@NamedAttributeNode(value =
                "chats",subgraph = "chatSubgraph"),@NamedAttributeNode(value = "createdEvents",subgraph = "eventSubgraph")
        },subgraphs = {@NamedSubgraph(
        name="chatSubgraph",attributeNodes = {@NamedAttributeNode(value = "chat"),@NamedAttributeNode(value = "user")}
),@NamedSubgraph(name = "eventSubgraph",attributeNodes = {@NamedAttributeNode(value = "chat")})})
@NamedEntityGraph(name = "userGraph1",attributeNodes = {@NamedAttributeNode("authorities"),
        @NamedAttributeNode("surveyResult")
})
public class User {
    @ElementCollection()
    @ToString.Exclude
    private List<String> authorities;


    @Id
    @Column(unique = true, name = "login")
    private String login;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private boolean isPhoneConfirmed;

    private String password;

    private String pictureUrl;


    private LocalDate registrationDate;


    public User(String login) {
        this.login = login;
    }

    public User(String login, List<String> authoritiesInStrings) {
        this.login=login;
        this.authorities=authoritiesInStrings;
    }

    public User(String login,  String password, List<GrantedAuthority> authorities) {
        this.login = login;
        this.password = password;
        this.authorities = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
    }




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

    @OneToOne(mappedBy = "user",fetch = FetchType.LAZY)
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


    public User(List<String> authorities, String login,
                String firstName, String lastName, String email, String phone,
                String pictureUrl) {
        this.authorities = authorities;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.pictureUrl = pictureUrl;
    }

    public User(List<String> authorities, String login,
                String firstName, String lastName, String email, String phone,
                String pictureUrl,boolean oauthUser) {
        this.authorities = authorities;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.pictureUrl = pictureUrl;
        this.setOauthUser(oauthUser);
    }


}
