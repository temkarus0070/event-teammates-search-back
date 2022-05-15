package org.netcracker.eventteammatessearch.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "eventTypes")
public class EventType {
    @Id
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        EventType eventType = (EventType) o;
        return name != null && Objects.equals(name, eventType.name);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
