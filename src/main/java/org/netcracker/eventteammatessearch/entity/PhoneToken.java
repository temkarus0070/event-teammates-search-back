package org.netcracker.eventteammatessearch.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class PhoneToken {
    @EmbeddedId
    private PhoneTokenKey id;

    @ManyToOne
    @MapsId("userId")
    private User user;

    @MapsId("dateTimeSend")
    private LocalDateTime dateTimeSend;

    private String code;

}
