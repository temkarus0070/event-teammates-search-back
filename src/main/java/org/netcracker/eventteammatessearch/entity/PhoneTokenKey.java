package org.netcracker.eventteammatessearch.entity;

import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
@EqualsAndHashCode
public class PhoneTokenKey implements Serializable {

    private LocalDateTime dateTimeSend;

    private Long userId;
}
