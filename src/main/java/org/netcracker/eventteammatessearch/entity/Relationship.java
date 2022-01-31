package org.netcracker.eventteammatessearch.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Relationship {
    @EmbeddedId
    private RelationshipKey id;

    @ManyToOne
    @MapsId("user1Id")
    private User user1;

    @ManyToOne
    @MapsId("user2Id")
    private User user2;

    private boolean isFriend;
}
