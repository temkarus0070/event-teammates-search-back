package org.netcracker.eventteammatessearch.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String theme;

    @NonNull
    private String name;


    private String description;

    @NonNull
    private LocalDateTime dateTimeStart;


    private LocalDateTime dateTimeEnd;

    private int maxNumberOfGuests;

    private int price;

    @JsonProperty("isPrivate")
    private boolean isPrivate;

    @JsonProperty("isOnline")
    private boolean isOnline;

    @JsonProperty("isHidden")
    private boolean isHidden;

    private Long chatId;

    private String url;

    @Transient
    private double avgMark;

    @ManyToOne
    private Location location;

    @ManyToOne
    private User owner;

    @OneToMany(mappedBy = "event")
    private Set<EventAttendance> guests;

    @OneToMany(mappedBy = "event")
    private Set<Complaint> complaints;

    @ManyToMany
    private Set<User> invitedGuests;

    @ManyToMany(mappedBy = "events")
    private Set<Tag> tags;
}
