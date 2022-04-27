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
import org.netcracker.eventteammatessearch.entity.mongoDB.Review;
import org.netcracker.eventteammatessearch.persistence.repositories.EventAttendanceRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.EventRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.TagRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.mongo.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.geolatte.geom.builder.DSL.g;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;

@Component
public class EventsService {
    private static final Logger logger = LoggerFactory.getLogger(EventsService.class);


    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ReviewRepository reviewRepository;


    @Autowired
    private ReviewService reviewService;

    @Autowired
    private EventAttendanceRepository eventAttendanceRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ChatService chatService;

    private GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);

    private WKTReader wktReader = new WKTReader();



    public Event get(Long id) {
        Optional<Event> eventOptional = eventRepository.findById(id);
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            String login = event.getOwner().getLogin();
            try {
                double avgMark = reviewRepository.averageReviewNumber(login);
                event.setAvgMark(avgMark);
            } catch (NullPointerException nullPointerException) {
                logger.info("event owner marks have not been found", nullPointerException);
            }
            return event;
        } else throw new ObjectNotFoundException(id, "Event");
    }

    public List<Event> getFinishedEventsOfUser(Principal principal) {
        List<Event> allUserEndedEvents = eventRepository.findAllUserEndedEvents(principal.getName());
        List<Long> ids = allUserEndedEvents.stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Review> reviewMap = Stream.of(reviewRepository.getReviewsById_UserIdAndId_EventIdIn(principal.getName(), ids)).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.<Review, Long, Review>toMap(
                (Review e) -> e.getId().getEventId(),
                (Review e) -> e));
        allUserEndedEvents = allUserEndedEvents.stream().filter(e -> reviewMap.get(e.getId()) == null).collect(Collectors.toList());
        return allUserEndedEvents;
    }

    public List<Event> get() {
        List<Event> eventList = eventRepository.findAll();
        reviewService.setMarksToEvents(eventList);
        return eventList;
    }

    public List<Event> getUsersCreatedEventsByLogin(String userLogin) {
        return eventRepository.getUsersCreatedEventsByLogin(userLogin);
    }

    public List<Event> getUsersAttendedEventsByLogin(String userLogin) {
        return eventAttendanceRepository.getUsersAttendedEventsByLogin(userLogin);
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
    }

    public Set<String> getWords(String word) {
        return eventRepository.getWords(word);
    }

    public void delete(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    public void update(Event event, Principal principal) {
        Optional<Event> optionalEvent = this.eventRepository.findById(event.getId());
        if (optionalEvent.isPresent()) {
            if (optionalEvent.get().getOwner().getLogin().equals(principal.getName())) {
                event.setOwner(optionalEvent.get().getOwner());
                event.setChat(optionalEvent.get().getChat());
                event.setComplaints(optionalEvent.get().getComplaints());
                this.eventRepository.save(event);
            } else {
                logger.warn(String.format("Пользователь %s пытался редактировать не свой эвент с id = %d", principal.getName(), event.getId()));
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Вы не можете редактировать данное событие");
            }
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "НЕ НАЙДЕНО СОБЫТИЕ С id = " + event.getId());
    }

    public List<Event> getEventsByRadius(double lon, double lat, double radius, Principal principal) {
        Point p = factory.createPoint(new Coordinate(lon, lat));
        List<Event> nearWithinDistance = eventRepository.findNearWithinDistance(p, radius);
        if (principal != null) {
            nearWithinDistance.forEach(event -> {
                if (event.getGuests().parallelStream().anyMatch(eventAttendance -> eventAttendance.getUser().getLogin().equals(principal.getName())))
                    event.setCurrentUserEntered(true);
                if (event.getInvitedGuests().parallelStream().anyMatch(user -> user.getLogin().equals(principal.getName())))
                    event.setCurrentUserInvited(true);
            });
        }
        reviewService.setMarksToEvents(nearWithinDistance);
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

        specificationList.add((root, query, criteriaBuilder) -> {
                    Predicate predicate = null;
                    if (!filterData.isFreeEvents() && filterData.getPriceFrom() == 0 && filterData.getPriceTo() == 0)
                        predicate = criteriaBuilder.notEqual(root.get(Event_.price), 0);
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
        if (principal != null) {
            events.forEach(event -> {
                if (event.getGuests().parallelStream().anyMatch(eventAttendance -> eventAttendance.getUser().getLogin().equals(principal.getName())))
                    event.setCurrentUserEntered(true);
                if (event.getInvitedGuests().parallelStream().anyMatch(user -> user.getLogin().equals(principal.getName())))
                    event.setCurrentUserInvited(true);
            });
        }

        reviewService.setMarksToEvents(eventPage.toList());
        if (filterData.getEventOwnerRating() > 0) {
            List<Event> eventList = eventPage.toList().stream().filter(e -> e.getAvgMark() >= filterData.getEventOwnerRating()).collect(Collectors.toList());
            Page<Event> eventPage1 = new PageImpl<>(eventList, eventPage.getPageable(), eventPage.getTotalElements());
            return eventPage1;
        }
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

        specificationList.add((root, query, criteriaBuilder) -> {
                    Predicate predicate = null;
                    if (!filterData.isFreeEvents() && filterData.getPriceFrom() == 0 && filterData.getPriceTo() == 0)
                        predicate = criteriaBuilder.notEqual(root.get(Event_.price), 0);
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
        if (principal != null)
            events.forEach(event -> {
                if (event.getGuests().parallelStream().anyMatch(eventAttendance -> eventAttendance.getUser().getLogin().equals(principal.getName())))
                    event.setCurrentUserEntered(true);
                if (event.getInvitedGuests().parallelStream().anyMatch(user -> user.getLogin().equals(principal.getName())))
                    event.setCurrentUserInvited(true);
            });
        reviewService.setMarksToEvents(events);
        if (filterData.getEventOwnerRating() > 0) {
            events = events.stream().filter(e -> e.getAvgMark() >= filterData.getEventOwnerRating()).collect(Collectors.toList());
        }
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
        chatService.removeUserFromChat(principal.getName(), eventId);
        this.eventAttendanceRepository.deleteById(userEventKey);
    }

    public List<Event> getInvitesEvent(String name) { return eventRepository.findUsersInvitedEvents(name); }

    public void removeInvite(long eventId, String login){
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isPresent()){
            Event event = eventOptional.get();
            event.setInvitedGuests(event.getInvitedGuests().stream().filter(u->!u.getLogin().equals(login)).collect(Collectors.toSet()));
            eventRepository.save(event);
        }
    }
}
