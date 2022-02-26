package org.netcracker.eventteammatessearch.Services;

import org.hibernate.ObjectNotFoundException;
import org.netcracker.eventteammatessearch.entity.Event;
import org.netcracker.eventteammatessearch.entity.EventAttendance;
import org.netcracker.eventteammatessearch.entity.UserEventKey;
import org.netcracker.eventteammatessearch.persistence.repositories.EventAttendanceRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.EventRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.mongo.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;

@Component
public class EventsService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private EventAttendanceRepository eventAttendanceRepository;

    public Event get(Long id) {
        Event event = eventRepository.getById(id);
        double avgMark = reviewRepository.averageReviewNumber(event.getOwner().getLogin());
        event.setAvgMark(avgMark);
        return event;
    }


    public void assignOnEvent(Long eventId, Principal principal) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            EventAttendance eventAttendance = new EventAttendance();
            eventAttendance.setId(new UserEventKey(event.getId(), principal.getName()));
            eventAttendanceRepository.save(eventAttendance);
        } else throw new ObjectNotFoundException(eventId, "Event");
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
