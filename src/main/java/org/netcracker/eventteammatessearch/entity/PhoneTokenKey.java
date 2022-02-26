package org.netcracker.eventteammatessearch.entity;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
public class PhoneTokenKey implements Serializable {

    private LocalDateTime dateTimeSend;

    private String userId;
}
