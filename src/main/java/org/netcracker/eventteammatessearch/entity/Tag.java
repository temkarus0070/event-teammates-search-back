package org.netcracker.eventteammatessearch.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Tag {

    @Id
    private String name;


    @ManyToMany(mappedBy = "tags")
    @ToString.Exclude
    @JsonIgnore
    private Set<Event> events;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Tag tag = (Tag) o;
        return name != null && Objects.equals(name, tag.name);
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
}
