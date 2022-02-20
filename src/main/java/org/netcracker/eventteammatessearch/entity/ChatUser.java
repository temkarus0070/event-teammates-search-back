package org.netcracker.eventteammatessearch.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class ChatUser {
    @EmbeddedId
    private ChatUserKey id;

    @ManyToOne
    @MapsId("chatId")
    private Chat chat;

    @ManyToOne
    @MapsId("userId")
    private User user;

    @Enumerated
    private ChatUserType chatUserType;
}
