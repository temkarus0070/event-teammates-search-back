package org.netcracker.eventteammatessearch.entity;

import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
public class RelationshipKey implements Serializable {

    private Long user1Id;

    private Long user2Id;
}

