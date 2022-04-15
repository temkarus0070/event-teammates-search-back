package org.netcracker.eventteammatessearch.Services;

import org.geolatte.geom.builder.DSL;
import org.hibernate.ObjectNotFoundException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.WKTReader;
import org.netcracker.eventteammatessearch.appEntities.EventFilterData;
import org.netcracker.eventteammatessearch.entity.*;
import org.netcracker.eventteammatessearch.persistence.repositories.EventAttendanceRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.EventInvitedGuests;
import org.netcracker.eventteammatessearch.persistence.repositories.EventRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.TagRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.mongo.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.security.Principal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.geolatte.geom.builder.DSL.g;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;

@Component
public class EventsService {


    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private EventInvitedGuests invitesRepository;

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
            eventAttendance.setEvent(event);
            eventAttendance.setUser(new User(principal.getName()));
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
        if (event.getUrl() != null) {
            if (!(event.getUrl().contains("http://") || event.getUrl().contains("https://"))) {
                event.setUrl("http://" + event.getUrl());
            }
        }
        eventRepository.save(event);
        if (event.getUrl() != null) {
            Long tempId = eventRepository.findByUrl(event.getUrl()).getId();
            for (User tempUser: event.getInvitedGuests()) {
                Invite invite = new Invite();
                invite.setId(tempId);
                invite.setLogin(tempUser.getLogin());
                invitesRepository.save(invite);
            }
        }
    }

    public Set<String> getWords(String word) {
        return eventRepository.getWords(word);
    }

    public void delete(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    public void update(Event event) {
        this.eventRepository.save(event);
    }

    public List<Event> getEventsByRadius(double lon, double lat, double radius, Principal principal) {
        Point p = factory.createPoint(new Coordinate(lon, lat));
        List<Event> nearWithinDistance = eventRepository.findNearWithinDistance(p, radius);
        nearWithinDistance.forEach(event -> {
            if (event.getGuests().parallelStream().anyMatch(eventAttendance -> eventAttendance.getUser().getLogin().equals(principal.getName())))
                event.setCurrentUserEntered(true);
            if (event.getInvitedGuests().parallelStream().anyMatch(user -> user.getLogin().equals(principal.getName())))
                event.setCurrentUserInvited(true);
        });
        return nearWithinDistance;
    }


    public Page<Event> filterByPage(EventFilterData filterData, Principal principal, Pageable pageable) {
        List<Specification<Event>> specificationList = new ArrayList<>();
        specificationList.add((root, query, criteriaBuilder) -> getWordsSpec(root, query, criteriaBuilder, filterData.getKeyWords()));
        specificationList.add((root, query, criteriaBuilder) -> filterData.getEventType() == null ? null : criteriaBuilder.equal(root.get(Event_.EVENT_TYPE), filterData.getEventType()));
        specificationList.add((root, query, criteriaBuilder) -> filterData.getEventLengthFrom() != 0 || filterData.getEventLengthTo() != 0 ?
                criteriaBuilder.isTrue(criteriaBuilder.function("is_date_diff_between", Boolean.class, root.get(Event_.dateTimeStart), root.get(Event_.dateTimeEnd),
                        criteriaBuilder.literal(filterData.getEventLengthFrom()), criteriaBuilder.literal(filterData.getEventLengthTo()))) : null);

        specificationList.add((root, query, criteriaBuilder) -> {
            Predicate predicate = null;
            if (filterData.getEventBeginTimeFrom() != null)
                predicate = criteriaBuilder.greaterThanOrEqualTo(root.get(Event_.DATE_TIME_START), filterData.getEventBeginTimeFrom());
            if (filterData.getEventBeginTimeTo() != null) {
                Predicate predicate1 = criteriaBuilder.lessThanOrEqualTo(root.get(Event_.DATE_TIME_START), filterData.getEventBeginTimeTo());
                if (predicate != null) {
                    predicate = criteriaBuilder.and(predicate, predicate1);
                } else predicate = predicate1;
            }
            return predicate;
        });
        specificationList.add((root, query, criteriaBuilder) -> {
            Predicate predicate = null;
            if (filterData.getGuestsCountFrom() != 0) {
                predicate = criteriaBuilder.greaterThanOrEqualTo(root.get(Event_.maxNumberOfGuests), filterData.getGuestsCountFrom());
            }
            if (filterData.getGuestsCountTo() != 0) {
                Predicate predicate1 = criteriaBuilder.lessThanOrEqualTo(root.get(Event_.maxNumberOfGuests), filterData.getGuestsCountTo());
                if (filterData.getGuestsCountFrom() != 0) {
                    predicate = criteriaBuilder.and(predicate1, predicate);
                } else predicate = predicate1;
            }
            return predicate;
        });
        specificationList.add((root, query, criteriaBuilder) -> {
                    Predicate predicate = null;
                    if (filterData.getPriceFrom() != 0)
                        predicate = criteriaBuilder.greaterThanOrEqualTo(root.get(Event_.price), filterData.getPriceFrom());
                    if (filterData.getPriceTo() != 0) {
                        Predicate predicate1 = criteriaBuilder.lessThanOrEqualTo(root.get(Event_.price), filterData.getPriceTo());
                        if (filterData.getPriceFrom() != 0) {
                            predicate = criteriaBuilder.and(predicate1, predicate);
                        } else predicate = predicate1;
                    }
                    return predicate;
                }
        );
        specificationList.add((root, query, criteriaBuilder) -> filterData.getTheme() != null && !filterData.getTheme().equals("") ? criteriaBuilder.equal(root.get(Event_.theme), filterData.getTheme()) : null);
        specificationList.add((root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.equal(root.get(Event_.IS_ONLINE), filterData.getEventFormats().contains("ONLINE")),
                criteriaBuilder.notEqual(root.get(Event_.IS_ONLINE), filterData.getEventFormats().contains("OFFLINE"))));
        specificationList.add((root, query, criteriaBuilder) -> filterData.isFreeEvents() ? criteriaBuilder.equal(root.get(Event_.PRICE), 0) : null);
        double[] userLocation = filterData.getUserLocation();
        specificationList.add((root, query, criteriaBuilder) -> filterData.getUserLocation().length == 2 && filterData.getMaxDistance() != 0 ?
                org.hibernate.spatial.predicate.GeolatteSpatialPredicates.distanceWithin(criteriaBuilder, root.join(Event_.location).get("location"),
                        DSL.point(WGS84, g(userLocation[0], userLocation[1]))
                        , filterData.getMaxDistance()) : null);


        Specification<Event> endSpec = null;
        boolean isFirst = true;
        for (Specification<Event> eventSpecification : specificationList) {
            if (isFirst) {
                endSpec = eventSpecification;
                isFirst = false;
            } else endSpec = endSpec.and(eventSpecification);
        }
        Page<Event> eventPage = eventRepository.findAll(endSpec, pageable);
        List<Event> events = eventPage.getContent();
        events.forEach(event -> {
            if (event.getGuests().parallelStream().anyMatch(eventAttendance -> eventAttendance.getUser().getLogin().equals(principal.getName())))
                event.setCurrentUserEntered(true);
            if (event.getInvitedGuests().parallelStream().anyMatch(user -> user.getLogin().equals(principal.getName())))
                event.setCurrentUserInvited(true);
        });
        return eventPage;
    }


    public List<Event> filter(EventFilterData filterData, Principal principal) {
        List<Specification<Event>> specificationList = new ArrayList<>();
        specificationList.add((root, query, criteriaBuilder) -> getWordsSpec(root, query, criteriaBuilder, filterData.getKeyWords()));
        specificationList.add((root, query, criteriaBuilder) -> filterData.getEventType() == null ? null : criteriaBuilder.equal(root.get(Event_.EVENT_TYPE), filterData.getEventType()));
        specificationList.add((root, query, criteriaBuilder) -> filterData.getEventLengthFrom() != 0 || filterData.getEventLengthTo() != 0 ?
                criteriaBuilder.isTrue(criteriaBuilder.function("is_date_diff_between", Boolean.class, root.get(Event_.dateTimeStart), root.get(Event_.dateTimeEnd),
                        criteriaBuilder.literal(filterData.getEventLengthFrom()), criteriaBuilder.literal(filterData.getEventLengthTo()))) : null);

        specificationList.add((root, query, criteriaBuilder) -> {
            Predicate predicate = null;
            if (filterData.getEventBeginTimeFrom() != null)
                predicate = criteriaBuilder.greaterThanOrEqualTo(root.get(Event_.DATE_TIME_START), filterData.getEventBeginTimeFrom());
            if (filterData.getEventBeginTimeTo() != null) {
                Predicate predicate1 = criteriaBuilder.lessThanOrEqualTo(root.get(Event_.DATE_TIME_START), filterData.getEventBeginTimeTo());
                if (predicate != null) {
                    predicate = criteriaBuilder.and(predicate, predicate1);
                } else predicate = predicate1;
            }
            return predicate;
        });
        specificationList.add((root, query, criteriaBuilder) -> {
            Predicate predicate = null;
            if (filterData.getGuestsCountFrom() != 0) {
                predicate = criteriaBuilder.greaterThanOrEqualTo(root.get(Event_.maxNumberOfGuests), filterData.getGuestsCountFrom());
            }
            if (filterData.getGuestsCountTo() != 0) {
                Predicate predicate1 = criteriaBuilder.lessThanOrEqualTo(root.get(Event_.maxNumberOfGuests), filterData.getGuestsCountTo());
                if (filterData.getGuestsCountFrom() != 0) {
                    predicate = criteriaBuilder.and(predicate1, predicate);
                } else predicate = predicate1;
            }
            return predicate;
        });
        specificationList.add((root, query, criteriaBuilder) -> {
                    Predicate predicate = null;
                    if (filterData.getPriceFrom() != 0)
                        predicate = criteriaBuilder.greaterThanOrEqualTo(root.get(Event_.price), filterData.getPriceFrom());
                    if (filterData.getPriceTo() != 0) {
                        Predicate predicate1 = criteriaBuilder.lessThanOrEqualTo(root.get(Event_.price), filterData.getPriceTo());
                        if (filterData.getPriceFrom() != 0) {
                            predicate = criteriaBuilder.and(predicate1, predicate);
                        } else predicate = predicate1;
                    }
                    return predicate;
                }
        );
        specificationList.add((root, query, criteriaBuilder) -> filterData.getTheme() != null && !filterData.getTheme().equals("") ? criteriaBuilder.equal(root.get(Event_.theme), filterData.getTheme()) : null);
        specificationList.add((root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.equal(root.get(Event_.IS_ONLINE), filterData.getEventFormats().contains("ONLINE")),
                criteriaBuilder.notEqual(root.get(Event_.IS_ONLINE), filterData.getEventFormats().contains("OFFLINE"))));
        specificationList.add((root, query, criteriaBuilder) -> filterData.isFreeEvents() ? criteriaBuilder.equal(root.get(Event_.PRICE), 0) : null);
        double[] userLocation = filterData.getUserLocation();
        specificationList.add((root, query, criteriaBuilder) -> filterData.getUserLocation().length == 2 && filterData.getMaxDistance() != 0 ?
                org.hibernate.spatial.predicate.GeolatteSpatialPredicates.distanceWithin(criteriaBuilder, root.join(Event_.location).get("location"),
                        DSL.point(WGS84, g(userLocation[0], userLocation[1]))
                        , filterData.getMaxDistance()) : null);


        Specification<Event> endSpec = null;
        boolean isFirst = true;
        for (Specification<Event> eventSpecification : specificationList) {
            if (isFirst) {
                endSpec = eventSpecification;
                isFirst = false;
            } else endSpec = endSpec.and(eventSpecification);
        }
        List<Event> events = eventRepository.findAll(endSpec);
        events.forEach(event -> {
            if (event.getGuests().parallelStream().anyMatch(eventAttendance -> eventAttendance.getUser().getLogin().equals(principal.getName())))
                event.setCurrentUserEntered(true);
            if (event.getInvitedGuests().parallelStream().anyMatch(user -> user.getLogin().equals(principal.getName())))
                event.setCurrentUserInvited(true);
        });
        return events;
    }


    private Predicate getWordsSpec(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, List<String> words) {
        if (words == null || words.size() == 0)
            return null;
        else {
            Predicate predicate = null;
            for (String word : words) {

                Predicate like = criteriaBuilder.like(root.get(Event_.NAME), "%" + word + "%");
                like = criteriaBuilder.or(like, criteriaBuilder.like(root.get(Event_.DESCRIPTION), "%" + word + "%"));
                if (predicate == null) {
                    predicate = like;
                } else
                    predicate = criteriaBuilder.or(like, predicate);
            }
            return predicate;
        }
    }

    public void removeUserFromEvent(Principal principal, long eventId) {
        UserEventKey userEventKey = new UserEventKey(eventId, principal.getName());
        this.eventAttendanceRepository.deleteById(userEventKey);
    }


}
