package org.netcracker.eventteammatessearch.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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
    @Fetch(FetchMode.JOIN)
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

    @Fetch(FetchMode.JOIN)
    @OneToOne(mappedBy = "event", cascade = CascadeType.REMOVE)
    private Chat chat;

    private String url;

    @Transient
    private double avgMark;

    @Transient
    private boolean currentUserInvited;

    @Transient
    private boolean currentUserEntered;


    @Fetch(FetchMode.JOIN)
    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private Location location;

    @Fetch(FetchMode.JOIN)
    @ManyToOne
    private User owner;

    @Fetch(FetchMode.JOIN)
    @OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE,fetch = FetchType.EAGER)
    @ToString.Exclude
    private Set<EventAttendance> guests;

    @Fetch(FetchMode.JOIN)
    @OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE,fetch = FetchType.EAGER)
    @ToString.Exclude
    private Set<Complaint> complaints;

    @Fetch(FetchMode.JOIN)
    @ManyToMany(cascade = CascadeType.REFRESH,fetch = FetchType.EAGER)
    @ToString.Exclude
    private Set<User> invitedGuests;

    @Transient
    private boolean recommendedBySurvey;

    @Transient
    private long visitorsCount;

    @Fetch(FetchMode.JOIN)
    @ElementCollection(fetch = FetchType.EAGER)
    @ToString.Exclude
    private Set<String> tags;

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
