package org.netcracker.eventteammatessearch.Services;

import com.sun.security.auth.UserPrincipal;
import org.hibernate.ObjectNotFoundException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.WKTReader;
import org.netcracker.eventteammatessearch.appEntities.EventFilterData;
import org.netcracker.eventteammatessearch.dto.EventDto;
import org.netcracker.eventteammatessearch.entity.*;
import org.netcracker.eventteammatessearch.entity.mongoDB.Notification;
import org.netcracker.eventteammatessearch.entity.mongoDB.Review;
import org.netcracker.eventteammatessearch.entity.mongoDB.sequenceGenerators.SequenceGeneratorService;
import org.netcracker.eventteammatessearch.persistence.repositories.EventAttendanceRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.EventRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.SurveyRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.UserRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.mongo.NotificationsRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.mongo.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class EventsService {
    private static final Logger logger = LoggerFactory.getLogger(EventsService.class);

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private SurveyRepository surveyRepository;
    @Autowired
    private EventAttendanceRepository eventAttendanceRepository;
    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;
    @Autowired
    private NotificationsRepository notificationsRepository;
    @Autowired
    private SurveyFilterService surveyFilterService;
    @Autowired
    private EventFilterOnSpecificationsService eventFilterOnSpecificationsService;
    @Autowired
    private ChatService chatService;

    private GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
    private WKTReader wktReader = new WKTReader();

    public EventDto get(Long id) {
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
            return getDTOS(List.of(event)).get(0);
        } else throw new ObjectNotFoundException(id, "Event");
    }

    public Event getOne(Long id) {
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
            return eventOptional.get();
        } else throw new ObjectNotFoundException(id, "Event");
    }

    public List<EventDto> getFinishedEventsOfUser(Principal principal) {
        List<Event> allUserEndedEvents = eventRepository.findAllUserEndedEvents(principal.getName());
        reviewService.setMarksToEvents(allUserEndedEvents);
        return getDTOS(allUserEndedEvents);
    }

    private void hideEventsUrlInList(List<Event> eventList, Principal principal) {
        eventList.forEach(e -> {
            var url = e.getUrl();
            if (e.isOnline() && e.getDateTimeStart().isAfter(LocalDateTime.now())) {
                url = "";
            } else if (e.isOnline() && e.getDateTimeStart().isBefore(LocalDateTime.now()) && e.getGuests().stream().noneMatch(ev -> ev.getUser().getLogin().equals(principal.getName()))) {
                url = "";
            }
            if (e.isOnline() && e.getOwner().getLogin().equals(principal.getName())) {
                url = e.getUrl();
            }

            e.setUrl(url);
        });

    }

    public List<EventDto> getFinishedEventsOfUserWithoutReviews(Principal principal) {
        List<Event> allUserEndedEvents = eventRepository.findAllUserEndedEvents(principal.getName());
        List<Long> ids = allUserEndedEvents.stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Review> reviewMap = reviewService.findReviewsOfUser(principal).stream().collect(Collectors.toMap((e) -> e.getId().getEventId(), (val) -> val));
        allUserEndedEvents = allUserEndedEvents.stream().filter(e -> !reviewMap.containsKey(e.getId())).collect(Collectors.toList());
        return getDTOS(allUserEndedEvents);
    }

    public List<EventDto> getFinishedEventsOfUserInInterval(Principal principal, LocalDateTime date1, LocalDateTime date2) {
        List<Event> allUserEndedEvents = eventRepository.findAllUserEndedEventsInInterval(principal.getName(), date1, date2);
        List<Long> ids = allUserEndedEvents.stream().map(Event::getId).collect(Collectors.toList());

        reviewService.setMarksToEvents(allUserEndedEvents);
        return getDTOS(allUserEndedEvents);
    }

    public List<Event> get() {
        List<Event> eventList = eventRepository.findAll();
        reviewService.setMarksToEvents(eventList);

        return eventList;
    }


    public EventAttendance assignOnEvent(Long eventId, Principal principal) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            if (event.getDateTimeEnd() != null && event.getDateTimeEnd().isBefore(LocalDateTime.now())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Данное событие уже закончилось");
            }
            if (event.getMaxNumberOfGuests() == 0 || event.getGuests().size() < event.getMaxNumberOfGuests()) {
                EventAttendance eventAttendance = new EventAttendance();
                eventAttendance.setId(new UserEventKey(event.getId(), principal.getName()));
                eventAttendance.setEvent(event);
                eventAttendance.setUser(new User(principal.getName()));
                event.setInvitedGuests(event.getInvitedGuests().stream().filter(e -> !e.getLogin().equals(principal.getName())).collect(Collectors.toSet()));
                eventAttendanceRepository.save(eventAttendance);
                return eventAttendance;

            } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Превышен лимит участников мероприятия");
        } else throw new ObjectNotFoundException(eventId, "Event");
    }


    public void add(Event event, Principal principal) {
        String name = principal.getName();
        User user = userRepository.getUserByLogin(name);
        if (user.isBlocked()) {
            throw new AccessDeniedException("Данная возможность заблокирована для вашего аккаунта");
        }
        event.setOwner(user);
        if (event.getUrl() != null) {
            if (!(event.getUrl().contains("http://") || event.getUrl().contains("https://"))) {
                event.setUrl("http://" + event.getUrl());
            }
        }
        eventRepository.save(event);
    }

    public List<EventDto> getUsersCreatedEventsByLogin(String userLogin) {
        List<Event> usersCreatedEventsByLogin = eventRepository.getUsersCreatedEventsByLogin(userLogin);
        reviewService.setMarksToEvents(usersCreatedEventsByLogin);
        List<EventDto> dtos = getDTOS(usersCreatedEventsByLogin);
        return dtos;
    }

    public List<EventDto> getUsersAttendedEventsByLogin(String userLogin) {
        List<Event> usersAttendedEventsByLogin = eventAttendanceRepository.getUsersAttendedEventsByLogin(userLogin);
        hideEventsUrlInList(usersAttendedEventsByLogin, new UserPrincipal(userLogin));
        reviewService.setMarksToEvents(usersAttendedEventsByLogin);
        return getDTOS(usersAttendedEventsByLogin);
    }


    public Set<String> getWords(String word) {
        return eventRepository.getWords(word);
    }

    public void delete(Long eventId) {
        notifyGuestsAboutRemove(eventId);
        eventRepository.deleteById(eventId);
    }

    public void notifyGuestsAboutRemove(long eventId) {
        Event event = eventRepository.getById(eventId);
        if (event != null) {
            List<Notification> notifications = new ArrayList<>();
            if (event.getGuests() != null)
                event.getGuests().forEach(g -> {
                    Notification notification = new Notification();
                    notification.setId(sequenceGeneratorService.generateSequence(Notification.SEQUENCE_NAME));
                    notification.setTitle(String.format("Событие %s удалено", event.getName()));
                    notification.setUserId(g.getId().getUserId());
                    notification.setDescription("Ваше участие аннулировано");
                    notifications.add(notification);
                });
            notificationsRepository.saveAll(notifications);
        }
    }

    public void notifyGuestsAboutUpdate(Event event) {
        if (event != null) {
            List<Notification> notifications = new ArrayList<>();
            if (event.getGuests() != null)
                event.getGuests().forEach(g -> {
                    Notification notification = new Notification();
                    notification.setId(sequenceGeneratorService.generateSequence(Notification.SEQUENCE_NAME));
                    notification.setTitle(String.format("Событие %s было обновлено", event.getName()));
                    notification.setUserId(g.getId().getUserId());
                    notification.setDescription("Данные мероприятия поменялись");
                    notifications.add(notification);
                });
            notificationsRepository.saveAll(notifications);
        }
    }


    public void update(Event event, Principal principal) {
        Optional<Event> optionalEvent = this.eventRepository.findById(event.getId());
        if (optionalEvent.isPresent()) {
            if (optionalEvent.get().isHidden()) {
                logger.warn(String.format("Пользователь %s пытался редактировать заблокированный эвент с id = %d", principal.getName(), event.getId()));
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Вы не можете редактировать данное событие");
            }

            if (optionalEvent.get().getOwner().getLogin().equals(principal.getName())) {
                event.setOwner(optionalEvent.get().getOwner());
                event.setChat(optionalEvent.get().getChat());
                event.setComplaints(optionalEvent.get().getComplaints());
                event.setGuests(optionalEvent.get().getGuests());
                notifyGuestsAboutUpdate(event);
                this.eventRepository.save(event);
            } else {
                logger.warn(String.format("Пользователь %s пытался редактировать не свой эвент с id = %d", principal.getName(), event.getId()));
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Вы не можете редактировать данное событие");
            }
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "НЕ НАЙДЕНО СОБЫТИЕ С id = " + event.getId());
    }

    public List<EventDto> getEventsByRadius(double lon, double lat, double radius, Principal principal) {
        Point p = factory.createPoint(new Coordinate(lon, lat));
        List<Event> nearWithinDistance = eventRepository.findNearWithinDistance(p, radius);
        if (principal != null) {
            Survey survey = surveyRepository.findByUser_login(principal.getName());

            nearWithinDistance.forEach(event -> {
                if (survey != null) {
                    if (isEventFitSurvey(event, survey)) {
                        event.setRecommendedBySurvey(true);
                    }
                }
                if (event.getGuests().parallelStream().anyMatch(eventAttendance -> eventAttendance.getUser().getLogin().equals(principal.getName())))
                    event.setCurrentUserEntered(true);
                if (event.getInvitedGuests().parallelStream().anyMatch(user -> user.getLogin().equals(principal.getName())))
                    event.setCurrentUserInvited(true);
            });
        }
        reviewService.setMarksToEvents(nearWithinDistance);
        return getDTOS(nearWithinDistance);
    }

    private List<EventDto> getDTOS(List<Event> list) {
        return list.stream().map(EventDto::new).collect(Collectors.toList());
    }

    public boolean isEventFitSurvey(Event event, Survey survey) {
        return surveyFilterService.isFit(survey, event);
    }

    public Page<EventDto> filterByPage(EventFilterData filterData, Principal principal, Pageable pageable) {
        Specification<Event> endSpec = eventFilterOnSpecificationsService.getSpecification(filterData);
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

            Page<EventDto> eventPage1 = new PageImpl<>(getDTOS(eventList), eventPage.getPageable(), eventPage.getTotalElements());
            return eventPage1;
        }
        if (filterData.getEventFormats().contains("ONLINE"))
            hideEventsUrlInList(events, principal);
        return new PageImpl<>(getDTOS(eventPage.toList()));
    }


    public List<EventDto> filter(EventFilterData filterData, Principal principal) {
        Specification<Event> endSpec = eventFilterOnSpecificationsService.getSpecification(filterData);
        List<Event> events = eventRepository.findAll(endSpec);
        if (principal != null) {
            Survey survey = surveyRepository.findByUser_login(principal.getName());
            events.forEach(event -> {
                if (survey != null && isEventFitSurvey(event, survey)) {
                    event.setRecommendedBySurvey(true);
                }
                if (event.getGuests().parallelStream().anyMatch(eventAttendance -> eventAttendance.getUser().getLogin().equals(principal.getName())))
                    event.setCurrentUserEntered(true);
                if (event.getInvitedGuests().parallelStream().anyMatch(user -> user.getLogin().equals(principal.getName())))
                    event.setCurrentUserInvited(true);
            });
        }
        reviewService.setMarksToEvents(events);
        if (filterData.getEventOwnerRating() > 0) {
            events = events.stream().filter(e -> e.getAvgMark() >= filterData.getEventOwnerRating()).collect(Collectors.toList());
        }
        if (filterData.getEventFormats().contains("ONLINE"))
            hideEventsUrlInList(events, principal);
        return getDTOS(events);
    }


    public void removeUserFromEvent(Principal principal, long eventId) {
        UserEventKey userEventKey = new UserEventKey(eventId, principal.getName());
        chatService.removeUserFromChat(principal.getName(), eventId);
        this.eventAttendanceRepository.deleteById(userEventKey);
    }


    public List<Event> getInvitesEvent(String name) {
        return eventRepository.findUsersInvitedEvents(name);
    }

    public void removeInvite(long eventId, String login) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            event.setInvitedGuests(event.getInvitedGuests().stream().filter(u -> !u.getLogin().equals(login)).collect(Collectors.toSet()));
            eventRepository.save(event);
        }
    }

}
