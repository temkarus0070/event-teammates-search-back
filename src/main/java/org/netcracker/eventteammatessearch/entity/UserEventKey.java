package org.netcracker.eventteammatessearch.entity;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;


@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserEventKey implements Serializable {

    private Long eventId;

    private String userId;
}
