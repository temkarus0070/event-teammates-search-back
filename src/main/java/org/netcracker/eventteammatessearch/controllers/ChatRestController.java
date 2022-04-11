package org.netcracker.eventteammatessearch.controllers;

import org.netcracker.eventteammatessearch.Services.ChatService;
import org.netcracker.eventteammatessearch.dao.ChatDAO;
import org.netcracker.eventteammatessearch.entity.Chat;
import org.netcracker.eventteammatessearch.entity.Event;
import org.netcracker.eventteammatessearch.entity.mongoDB.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatRestController {
    @Autowired
    private ChatService chatService;

    @PostMapping("/createEventChat")
    public long createEventChat(@RequestBody Event event, Principal principal) {
        return this.chatService.createForEvent(event, principal);
    }

    @GetMapping
    public Chat get(@RequestParam long chatId) {
        return this.chatService.get(chatId).get();
    }


    @GetMapping("/getMessages")
    public List<Message> getMessagesFromChat(@RequestParam long chatId) {
        return this.chatService.getMessages(chatId);
    }

    @GetMapping("/getCurrentChats")
    public List<ChatDAO> getAllChatsOfUser(Principal principal) {
        return chatService.getUserChats(principal);
    }
}
