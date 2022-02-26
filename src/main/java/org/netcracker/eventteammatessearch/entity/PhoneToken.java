package org.netcracker.eventteammatessearch.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Entity
@Data
@NoArgsConstructor
public class PhoneToken {
    @EmbeddedId
    private PhoneTokenKey id;

    @ManyToOne
    @MapsId("userId")
    private User user;


    private String code;

}
