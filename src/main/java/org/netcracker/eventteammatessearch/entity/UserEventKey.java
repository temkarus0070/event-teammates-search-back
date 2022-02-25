package org.netcracker.eventteammatessearch.entity;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;


@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
public class UserEventKey implements Serializable {

    private Long eventId;

    private Long userId;
}
