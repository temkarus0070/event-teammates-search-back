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


    @PostMapping("/createChatWithUser")
    public long createChatWithUser(@RequestParam String username, Principal principal) {
        return this.chatService.createForUser(username, principal);
    }


    @GetMapping
    public Chat get(@RequestParam long chatId, Principal principal) {
        return this.chatService.get(chatId, principal).get();
    }


    @GetMapping("/getMessages")
    public List<Message> getMessagesFromChat(@RequestParam long chatId) {
        return this.chatService.getMessages(chatId);
    }

    @GetMapping("/getCurrentChats")
    public List<ChatDAO> getAllChatsOfUser(Principal principal) {
        return chatService.getUserChats(principal);
    }

    @PatchMapping("/updateLastReadMessage")
    public void updateLastReadedMessage(Principal principal, @RequestParam long messageId, @RequestParam long chatId) {
        this.chatService.updateLastSeenMessage(principal, chatId, messageId);
    }

    @GetMapping("/getMessageBefore")
    public Message getMessageBefore(@RequestParam long chatId, @RequestParam long id) {
        return chatService.getMessageBefore(chatId, id);
    }
}
