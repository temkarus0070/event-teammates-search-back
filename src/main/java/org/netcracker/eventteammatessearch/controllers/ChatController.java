package org.netcracker.eventteammatessearch.controllers;

import org.netcracker.eventteammatessearch.entity.mongoDB.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatController {
    @MessageMapping("/sendMessage/{chatId}")
    @SendTo("/chat/messages/{chatId}")
    public Message sendMessage(@DestinationVariable long chatId, @Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
        Principal user = headerAccessor.getUser();
        message.setUserId(user.getName());
        return message;
    }

}
