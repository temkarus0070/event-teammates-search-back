package org.netcracker.eventteammatessearch.controllers;

import org.netcracker.eventteammatessearch.Services.EventsService;
import org.netcracker.eventteammatessearch.entity.Event;
import org.netcracker.eventteammatessearch.entity.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequestMapping(value = "/api/events")
@RestController
public class EventsController {
    @Autowired
    private EventsService eventsService;

    @PostMapping
    public void add(@RequestBody Event event) {
        eventsService.add(event);
    }

    @GetMapping("/getEventsWithinRadius")
    public List<Location> getEventsWithinRadius(@RequestParam double lon, @RequestParam double lat, @RequestParam int radius) {
        return eventsService.getEventsByRadius(lon, lat, radius);
    }

    @GetMapping("/getEvents")
    public List<Event> getEvents() {
        return eventsService.get();
    }

    @PostMapping("/assignOnEvent")
    public void assignOnEvents(@RequestBody Long eventId, Principal principal) {
        eventsService.assignOnEvent(eventId, principal);
    }

    @GetMapping("/getEvent")
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
