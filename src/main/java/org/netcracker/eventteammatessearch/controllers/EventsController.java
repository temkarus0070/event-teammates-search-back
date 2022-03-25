package org.netcracker.eventteammatessearch.controllers;

import org.netcracker.eventteammatessearch.Services.EventsService;
import org.netcracker.eventteammatessearch.appEntities.EventFilterData;
import org.netcracker.eventteammatessearch.entity.Event;
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
    public void add(@RequestBody Event event, Principal principal) {
        eventsService.add(event, principal);
    }

    @GetMapping("/getEventsWithinRadius")
    public List<Event> getEventsWithinRadius(@RequestParam double lon, @RequestParam double lat, @RequestParam double radius) {
        return eventsService.getEventsByRadius(lon, lat, radius);
    }

    @GetMapping("/getEvents")
    public List<Event> getEvents() {
        return eventsService.get();
    }

    @PostMapping("/assignOnEvent")
    public void assignOnEvents(@RequestParam long eventId, Principal principal) {
        eventsService.assignOnEvent(eventId, principal);
    }

    @GetMapping("/getEvent")
    public Event get(@RequestParam Long eventId) {
        return eventsService.get(eventId);
    }

    @PostMapping("/filter")
    public List<Event> filter(@RequestBody EventFilterData filterData, Principal principal) {
        return eventsService.filter(filterData, principal);
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
