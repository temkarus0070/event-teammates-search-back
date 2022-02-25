package org.netcracker.eventteammatessearch.Services;

import org.netcracker.eventteammatessearch.entity.Event;
import org.netcracker.eventteammatessearch.persistence.repositories.EventRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.mongo.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventsService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    public Event get(Long id) {
        Event event = eventRepository.getById(id);
        double avgMark = reviewRepository.averageReviewNumber(event.getOwner().getId());
        event.setAvgMark(avgMark);
        return event;
    }

    public void add(Event event) {
        eventRepository.save(event);
    }

    public void delete(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    public void update(Event event) {
        this.eventRepository.save(event);
    }


}
