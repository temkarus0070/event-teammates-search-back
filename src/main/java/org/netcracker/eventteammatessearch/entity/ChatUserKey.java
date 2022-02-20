package org.netcracker.eventteammatessearch.entity;

import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
public class ChatUserKey implements Serializable{

    private Long chatId;

    private Long userId;

}
