package org.netcracker.eventteammatessearch.controllers;

import org.netcracker.eventteammatessearch.Services.EventsService;
import org.netcracker.eventteammatessearch.appEntities.EventFilterData;
import org.netcracker.eventteammatessearch.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequestMapping(value = "/api/events")
@RestController
@PreAuthorize("isAuthenticated() || isAnonymous()")
public class EventsController {
    @Autowired
    private EventsService eventsService;





    /* add event */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public void add(@RequestBody Event event, Principal principal) {
        eventsService.add(event, principal);
    }








    @GetMapping("/getEventsWithinRadius")
    public List<Event> getEventsWithinRadius(@RequestParam double lon, @RequestParam double lat, @RequestParam double radius, Principal principal) {
        return eventsService.getEventsByRadius(lon, lat, radius, principal);
    }

    @GetMapping("/getWords")
    public Set<String> getWords(@RequestParam String word) {
        return eventsService.getWords(word);
    }

    @GetMapping("/getEvents")
    public List<Event> getEvents() {
        return eventsService.get();
    }

    @PostMapping("/assignOnEvent")
    @PreAuthorize("isAuthenticated()")
    public Map assignOnEvents(@RequestParam long eventId, Principal principal) {
        eventsService.assignOnEvent(eventId, principal);
        return Map.of("response", principal.getName());
    }

    @GetMapping("/getEvent")
    public Event get(@RequestParam Long eventId) {
        return eventsService.get(eventId);
    }

    @PostMapping("/filter")
    public List<Event> filter(@RequestBody EventFilterData filterData, Principal principal) {
        return eventsService.filter(filterData, principal);
    }

    @PostMapping("/filterWithPaging")
    public Page<Event> filterWithPaging(@RequestBody EventFilterData filterData, Principal principal, @RequestParam int pageNum, @RequestParam int size) {
        Pageable pageable = PageRequest.of(pageNum, size);
        Page<Event> eventPage = eventsService.filterByPage(filterData, principal, pageable);
        return eventPage;
    }

    @PatchMapping
    @PreAuthorize("isAuthenticated() && #event.owner.login.equals(#principal.name)")
    public void update(@RequestBody Event event, Principal principal) {
        eventsService.update(event);
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated() && eventsService.get(#eventId).owner.login.equals(#principal.name)")
    public void delete(@RequestParam Long eventId, Principal principal) {
        eventsService.delete(eventId);
    }

    @DeleteMapping("/deleteCurrentUserFromEvent")
    @PreAuthorize("isAuthenticated()")
    public Map deleteCurrentUserFromEvent(Principal principal, @RequestParam long eventId) {
        this.eventsService.removeUserFromEvent(principal, eventId);
        return Map.of("response", principal.getName());
    }
}
