package org.netcracker.eventteammatessearch.controllers;

import org.netcracker.eventteammatessearch.entity.EventType;
import org.netcracker.eventteammatessearch.persistence.repositories.EventTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/eventTypes")
public class EventTypeController {
    @Autowired
    private EventTypeRepository eventTypeRepository;

    @GetMapping
    public List<EventType> get() {
        return eventTypeRepository.findAll();
    }
}
