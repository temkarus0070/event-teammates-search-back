package org.netcracker.eventteammatessearch.dao;

import lombok.*;
import org.netcracker.eventteammatessearch.entity.Chat;
import org.netcracker.eventteammatessearch.entity.ChatUser;
import org.netcracker.eventteammatessearch.entity.Event;
import org.netcracker.eventteammatessearch.entity.User;
import org.netcracker.eventteammatessearch.entity.mongoDB.Message;

import java.util.List;


@Getter
@Setter
@ToString
@NoArgsConstructor

public class ChatDAO {

    private Long id;

    private String name;

    private boolean isPrivate;

    private Event event;

    private Message message;

    private List<ChatUser> chatUsers;

    private long lastReadMessage;

    private long unReadCount;

    public ChatDAO(Long id, String name, boolean isPrivate, Event event, Message message, List<ChatUser> chatUsers, long lastReadMessage, long unReadCount) {
        this.id = id;
        this.name = name;
        this.isPrivate = isPrivate;
        if (event!=null)
            this.event = new Event(event.getName());
        this.message = message;
        this.chatUsers = chatUsers;
        this.lastReadMessage = lastReadMessage;
        this.unReadCount = unReadCount;
        this.chatUsers.forEach(e->e.setUser(new User(e.getId().getUserId(),e.getUser().getFirstName(),e.getUser().getLastName(),e.getUser().getPictureUrl())));
    }

    public ChatDAO(Chat chat){
        if (chat.getEvent()!=null){
            this.event=new Event(chat.getEvent().getName());
        }
        chat.getChatUsers().forEach(c->{
            c.setUser(new User(c.getId().getUserId(),c.getUser().getFirstName(),c.getUser().getLastName(),c.getUser().getPictureUrl()));
        });
    }
}
