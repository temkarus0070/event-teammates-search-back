package org.netcracker.eventteammatessearch.Services;

import org.hibernate.ObjectNotFoundException;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.locationtech.jts.io.WKTReader;
import org.netcracker.eventteammatessearch.appEntities.EventFilterData;
import org.netcracker.eventteammatessearch.entity.Location;
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
import java.util.*;
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
        return eventRepository.findAll();
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
        List<Specification<Event>> specificationList = new ArrayList<>();
        specificationList.add((root, query, criteriaBuilder) -> getWordsSpec(root, query, criteriaBuilder, filterData.getKeyWords()));
        specificationList.add((root, query, criteriaBuilder) -> filterData.getEventType() == null ? null : criteriaBuilder.equal(root.get(Event_.EVENT_TYPE), filterData.getEventType()));
        specificationList.add((root, query, criteriaBuilder) -> filterData.getEventLengthFrom() != 0 || filterData.getEventLengthTo() != 0 ? criteriaBuilder.between(criteriaBuilder.diff(root.get(Event_.DATE_TIME_END),
                root.get(Event_.DATE_TIME_START)).as(Long.class), filterData.getEventLengthFrom(), filterData.getEventLengthTo()) : null);

        specificationList.add((root, query, criteriaBuilder) -> filterData.getEventBeginTimeFrom() != null || filterData.getEventBeginTimeTo() != null ?
                criteriaBuilder.between(root.get(Event_.DATE_TIME_START), filterData.getEventBeginTimeFrom(), filterData.getEventBeginTimeTo()) : null);
        specificationList.add((root, query, criteriaBuilder) -> filterData.getGuestsCountFrom() != 0 || filterData.getGuestsCountTo() != 0 ? criteriaBuilder.between(root.get(Event_.maxNumberOfGuests), filterData.getGuestsCountFrom(), filterData.getGuestsCountTo()) : null);
        specificationList.add((root, query, criteriaBuilder) -> filterData.getPriceFrom() != 0 || filterData.getPriceTo() != 0 ? criteriaBuilder.between(root.get(Event_.price), filterData.getPriceFrom(), filterData.getPriceTo()) : null);
        specificationList.add((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Event_.theme), filterData.getTheme()));
        specificationList.add((root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.equal(root.get(Event_.IS_ONLINE), filterData.getEventFormats().contains("ONLINE")),
                criteriaBuilder.notEqual(root.get(Event_.IS_ONLINE), filterData.getEventFormats().contains("OFFLINE"))));
        double[] userLocation = filterData.getUserLocation();
        specificationList.add((root, query, criteriaBuilder) ->
                org.hibernate.spatial.predicate.GeolatteSpatialPredicates.distanceWithin(criteriaBuilder, root.join(Event_.location).get("location"),
                        new org.locationtech.jts.geom.Point(new CoordinateArraySequence(new Coordinate[]{new CoordinateXY(userLocation[0], userLocation[1]})
                                , factory), filterData.getMaxDistance()));


        Specification<Event> endSpec = null;
        return eventRepository.findAll()

        return null;
    }


    private Predicate getWordsSpec(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, List<String> words) {
        if (words == null || words.size() == 0)
            return null;
        else {
            Predicate predicate = null;
            for (String word : words) {

                Predicate like = criteriaBuilder.like(root.get(Event_.NAME), "%" + word + "%");
                if (predicate == null) {
                    predicate = like;
                } else
                    predicate = criteriaBuilder.or(like, predicate);
            }
            return predicate;
        }
    }


}
