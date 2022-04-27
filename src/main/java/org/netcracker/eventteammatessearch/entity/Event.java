package org.netcracker.eventteammatessearch.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String theme;

    @NonNull
    private String name;

    @ManyToOne
    private EventType eventType;


    private String description;

    @NonNull
    private LocalDateTime dateTimeStart;


    private LocalDateTime dateTimeEnd;

    private int maxNumberOfGuests;

    private float price;

    private boolean hasChatWithOwner;

    @JsonProperty("isPrivate")
    private boolean isPrivate;

    @JsonProperty("isOnline")
    private boolean isOnline;

    @JsonProperty("isHidden")
    private boolean isHidden;

    @OneToOne(mappedBy = "event", cascade = CascadeType.REMOVE)
    private Chat chat;

    private String url;

    @Transient
    private double avgMark;

    @Transient
    private boolean currentUserInvited;

    @Transient
    private boolean currentUserEntered;


    @ManyToOne(cascade = CascadeType.ALL)
    private Location location;

    @ManyToOne

    private User owner;

    @OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE)
    @ToString.Exclude

    private Set<EventAttendance> guests;

    @OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private Set<Complaint> complaints;

    @ManyToMany(cascade = CascadeType.REFRESH)
    @ToString.Exclude

    private Set<User> invitedGuests;


    @ManyToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<Tag> tags;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Event event = (Event) o;
        return id != null && Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


}
