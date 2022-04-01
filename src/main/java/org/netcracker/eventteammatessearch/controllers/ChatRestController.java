package org.netcracker.eventteammatessearch.controllers;

import org.netcracker.eventteammatessearch.entity.mongoDB.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatRestController {
    @GetMapping
    public List<Message> getMessages() {

    }
}
