package org.netcracker.eventteammatessearch.entity;

import lombok.*;
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
@NamedEntityGraph(name = "relation-graph",includeAllAttributes = true,attributeNodes = {@NamedAttributeNode(value = "owner",subgraph = "user-sub"),
        @NamedAttributeNode(value = "friend",subgraph = "user-sub")}
,subgraphs = {@NamedSubgraph(name = "user-sub",attributeNodes = {@NamedAttributeNode("surveyResult")})})
public class Relationship {

    @MapsId("ownerId")
    @OneToOne()
    private User owner;

    @MapsId("friendId")
    @OneToOne()
    private User friend;

    @EmbeddedId
    private RelationshipId id;

    private boolean isItFriend;



    @Embeddable
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class RelationshipId implements Serializable {
        private String friendId;
        private String ownerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Relationship that = (Relationship) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
