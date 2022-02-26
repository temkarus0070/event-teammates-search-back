package org.netcracker.eventteammatessearch.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@Table(name = "app_users")
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    @NonNull
    private String login;

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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
