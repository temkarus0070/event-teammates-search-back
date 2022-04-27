package org.netcracker.eventteammatessearch.controllers;

import org.netcracker.eventteammatessearch.Services.EventsService;
import org.netcracker.eventteammatessearch.appEntities.EventFilterData;
import org.netcracker.eventteammatessearch.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequestMapping(value = "/api/events")
@RestController
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

    @GetMapping("/getUsersCreatedEventsByLogin")
    public List<Event> getUsersCreatedEventsByLogin(@RequestParam String userLogin) {
        return eventsService.getUsersCreatedEventsByLogin(userLogin);
    }

    @GetMapping("/getUsersAttendedEventsByLogin")
    public List<Event> getUsersAttendedEventsByLogin(@RequestParam String userLogin) {
        return eventsService.getUsersAttendedEventsByLogin(userLogin);
    }

    @PostMapping("/assignOnEvent")
    @PreAuthorize("isAuthenticated()")
    public Map assignOnEvents(@RequestParam long eventId, Principal principal) {
        eventsService.assignOnEvent(eventId, principal);
        return Map.of("response", principal.getName());
    }

    @GetMapping("/getEndedEvents")
    public List<Event> getUserEndedEvents(Principal principal) {
        return eventsService.getFinishedEventsOfUser(principal);
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
    @PreAuthorize("isAuthenticated()")
    public void update(@RequestBody Event event, Principal principal) {
        eventsService.update(event, principal);
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public void delete(@RequestParam Long eventId, Authentication principal) {
        eventsService.delete(eventId);
    }

    @DeleteMapping("/deleteCurrentUserFromEvent")
    @PreAuthorize("isAuthenticated()")
    public Map deleteCurrentUserFromEvent(Principal principal, @RequestParam long eventId) {
        this.eventsService.removeUserFromEvent(principal, eventId);
        return Map.of("response", principal.getName());
    }

    @GetMapping("/getInvitesEvent")
    public List<Event> getInvitesEvent(Principal principal) { return eventsService.getInvitesEvent(principal.getName()); }

    @GetMapping("/getInvitesEventCheck")
    public boolean isInvited(Principal principal) {
        if (eventsService.getInvitesEvent(principal.getName()).size() != 0) return true;
        else return false;
    }

    @PostMapping("/rejectInvite")
    @PreAuthorize("isAuthenticated()")
    public void rejectInvite(@RequestParam long eventId, Principal principal) {
        System.out.println("\n\n\n\n reject in controller \n\n\n\n");
        eventsService.removeInvite(eventId, principal.getName());
    }
}
