package org.netcracker.eventteammatessearch.controllers;

import org.netcracker.eventteammatessearch.Services.EventsService;
import org.netcracker.eventteammatessearch.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(value = "/events")
public class EventsController {
    @Autowired
    private EventsService eventsService;

    @PostMapping
    public void add(@RequestBody Event event) {
        eventsService.add(event);
    }

    @PostMapping("/assignOnEvent")
    public void assignOnEvents(@RequestBody Long eventId, Principal principal) {
        eventsService.assignOnEvent(eventId, principal);
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
