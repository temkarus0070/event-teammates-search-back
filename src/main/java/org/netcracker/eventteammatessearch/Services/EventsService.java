package org.netcracker.eventteammatessearch.Services;

import org.hibernate.ObjectNotFoundException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.WKTReader;
import org.netcracker.eventteammatessearch.entity.*;
import org.netcracker.eventteammatessearch.persistence.repositories.EventAttendanceRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.EventRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.mongo.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Component
public class EventsService {


    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private EventAttendanceRepository eventAttendanceRepository;

    private GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);

    private WKTReader wktReader = new WKTReader();


    public Event get(Long id) {
        Event event = eventRepository.getById(id);
        double avgMark = reviewRepository.averageReviewNumber(event.getOwner().getLogin());
        event.setAvgMark(avgMark);
        return event;
    }

    public List<Event> get() {
        List<Event> all = eventRepository.findAll();
        return all;
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

    public void add(Event event, Principal principal) {
        String name = principal.getName();
        User user = new User();
        user.setLogin(name);
        event.setOwner(user);
        eventRepository.save(event);
    }

    public void delete(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    public void update(Event event) {
        this.eventRepository.save(event);
    }

    public List<Location> getEventsByRadius(double lon, double lat, int radius) {
        Point p = factory.createPoint(new Coordinate(lon, lat));
        return eventRepository.findNearWithinDistance(p, radius);
    }


}
