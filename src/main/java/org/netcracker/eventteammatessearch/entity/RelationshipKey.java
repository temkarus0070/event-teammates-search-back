package org.netcracker.eventteammatessearch.entity;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
public class RelationshipKey implements Serializable {

    private Long user1Id;

    private Long user2Id;
}

