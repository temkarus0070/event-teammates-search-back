package org.netcracker.eventteammatessearch.controllers;

import org.netcracker.eventteammatessearch.entity.mongoDB.Message;
import org.netcracker.eventteammatessearch.persistence.repositories.mongo.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private MessageRepository messageRepository;

    @MessageMapping("/chat")
    public void processMessage(@Payload Message message) {
        message = messageRepository.save(message);
        messagingTemplate.convertAndSendToUser(String.valueOf(message.getChatId()), "/chatMessages", message.getId());
    }
}
