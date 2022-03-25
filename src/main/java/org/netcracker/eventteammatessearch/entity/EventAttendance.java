package org.netcracker.eventteammatessearch.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Entity
@Data
@NoArgsConstructor
public class EventAttendance {
    @EmbeddedId
    private UserEventKey id;

    @ManyToOne
    @MapsId("eventId")
    @JsonIgnore
    private Event event;

    @ManyToOne
    @MapsId("userId")
    @JsonIgnore
    private User user;
}
