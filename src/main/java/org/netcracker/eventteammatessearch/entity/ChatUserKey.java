package org.netcracker.eventteammatessearch.entity;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatUserKey implements Serializable{

    private Long chatId;

    private String userId;

}
