package org.netcracker.eventteammatessearch.Services;

import org.netcracker.eventteammatessearch.entity.Event;
import org.netcracker.eventteammatessearch.persistence.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventsService {
    @Autowired
    private EventRepository eventRepository;

    public Event get(Long id) {
        return eventRepository.getById(id);
    }

    public void add(Event event) {
        eventRepository.save(event);
    }

    public void delete(Event event) {
        eventRepository.delete(event);
    }

    public void update(Event event) {
        this.eventRepository.save(event);
    }


}
