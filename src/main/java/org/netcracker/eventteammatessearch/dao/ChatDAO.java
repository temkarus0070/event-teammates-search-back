package org.netcracker.eventteammatessearch.dao;

import lombok.*;
import org.netcracker.eventteammatessearch.entity.ChatUser;
import org.netcracker.eventteammatessearch.entity.Event;
import org.netcracker.eventteammatessearch.entity.mongoDB.Message;

import java.util.List;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChatDAO {

    private Long id;

    private String name;

    private boolean isPrivate;

    private Event event;

    private Message message;

    private List<ChatUser> chatUsers;

    private long lastReadMessage;

    private long unReadCount;
}
