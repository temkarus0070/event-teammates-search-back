package org.netcracker.eventteammatessearch.entity;

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

    @NonNull
    private String name;

    @NonNull
    private String description;

    @NonNull
    private LocalDateTime dateTimeStart;

    @NonNull
    private LocalDateTime dateTimeEnd;

    private int maxNumberOfGuests;

    private int price;

    private boolean isPrivate;

    private boolean isOnline;

    private boolean isHidden;

    private Long chatId;

    private String url;

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

    @ManyToMany
    private Set<ThemeTag> themeTags;
}
