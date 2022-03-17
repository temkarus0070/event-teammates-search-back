package org.netcracker.eventteammatessearch.Services;

import org.hibernate.ObjectNotFoundException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.WKTReader;
import org.netcracker.eventteammatessearch.appEntities.EventFilterData;
import org.netcracker.eventteammatessearch.entity.*;
import org.netcracker.eventteammatessearch.persistence.repositories.EventAttendanceRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.EventRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.TagRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.mongo.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EventsService {


    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private EventAttendanceRepository eventAttendanceRepository;

    @Autowired
    private TagRepository tagRepository;

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
        Set<Tag> tags = new HashSet<>(tagRepository.findAllById(event.getTags() == null ? new HashSet<>() : event.getTags().stream().map(Tag::getName).collect(Collectors.toList())));
        tags.addAll(event.getTags());
        event.setTags(tags);
        for (Tag tag : event.getTags()) {
            if (tag.getEvents() == null)
                tag.setEvents(new HashSet<>(List.of(event)));
            else tag.getEvents().add(event);
        }
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

    public List<Event> filter(EventFilterData filterData) {

        String[] words = filterData.getKeyWords().trim().split(" ");
        Specification<Event> specification = new Specification<Event>() {
            @Override
            public Predicate toPredicate(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.in(root.get(""))
            }
        }

    }


}
