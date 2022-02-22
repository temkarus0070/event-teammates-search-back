package org.netcracker.eventteammatessearch.controllers;

import org.netcracker.eventteammatessearch.Services.EventsService;
import org.netcracker.eventteammatessearch.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("/events")
public class EventsController {
    @Autowired
    private EventsService eventsService;

    @PostMapping
    public void add(@RequestBody Event event) {
        eventsService.add(event);
    }

    @GetMapping
    public Event get(@RequestParam Long eventId) {
        return eventsService.get(eventId);
    }

    @PatchMapping
    public void update(@RequestBody Event event) {
        eventsService.update(event);
    }

    @DeleteMapping
    public void delete(@RequestParam Long eventId) {
        eventsService.delete(eventId);
    }
}
