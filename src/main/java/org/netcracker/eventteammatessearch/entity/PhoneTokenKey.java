package org.netcracker.eventteammatessearch.entity;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class PhoneTokenKey implements Serializable {

    private LocalDateTime dateTimeSend;

    private String userId;
}
