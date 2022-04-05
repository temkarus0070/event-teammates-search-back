package org.netcracker.eventteammatessearch.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Complaint {
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

    private Status status;

    @ManyToOne
    @JsonIgnore
    private User admin;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Complaint complaint = (Complaint) o;
        return id != null && Objects.equals(id, complaint.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
