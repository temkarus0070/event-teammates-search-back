package org.netcracker.eventteammatessearch.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@Getter
@Setter
public class ChatUserKey implements Serializable{

    private Long chatId;

    private String userId;

}
