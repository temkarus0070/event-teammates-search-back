package org.netcracker.eventteammatessearch.entity;

import lombok.EqualsAndHashCode;

import javax.persistence.Embeddable;
import java.io.Serializable;


@Embeddable
@EqualsAndHashCode
public class UserEventKey implements Serializable {

    private Long eventId;

    private Long userId;
}
