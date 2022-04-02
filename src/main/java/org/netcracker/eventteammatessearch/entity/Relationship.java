package org.netcracker.eventteammatessearch.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"owner_login", "friend_login"})})
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Relationship {

    @EmbeddedId
    private RelationshipId id;

    private boolean isFriend;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Relationship that = (Relationship) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Embeddable
    @Getter
    @Setter
    public static class RelationshipId implements Serializable {
        @OneToOne()
        private User owner;

        @OneToOne()
        private User friend;
    }
}
