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
@AllArgsConstructor
@ToString
@NamedEntityGraph(
        name = "event-entity-graph",
        includeAllAttributes = true
)
@NamedEntityGraph(
        name = "event-entity-graph1",
        attributeNodes = {@NamedAttributeNode("invitedGuests")}
)
@NamedEntityGraph(
        name = "event-entity-graph2",
        attributeNodes = {@NamedAttributeNode("guests"),@NamedAttributeNode("chat"),@NamedAttributeNode("location"),
        @NamedAttributeNode("tags")}
)
@NamedEntityGraph(
        name = "event-entity-graph3",
        attributeNodes = {@NamedAttributeNode("invitedGuests"),@NamedAttributeNode("guests")}
)
public class Event {

    public Event(@NonNull String name) {
        this.name = name;
    }

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


    @OneToOne(mappedBy = "event", cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    private Chat chat;

    private String url;

    @Transient
    private double avgMark;

    @Transient
    private boolean currentUserInvited;

    @Transient
    private boolean currentUserEntered;



    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST},fetch = FetchType.LAZY)
    private Location location;


    @ManyToOne(fetch = FetchType.LAZY)
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

    @Transient
    private boolean recommendedBySurvey;

    @Transient
    private long visitorsCount;


    @ElementCollection()
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
