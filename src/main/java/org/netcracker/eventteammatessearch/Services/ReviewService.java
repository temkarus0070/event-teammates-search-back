package org.netcracker.eventteammatessearch.Services;

import org.hibernate.ObjectNotFoundException;
import org.netcracker.eventteammatessearch.entity.Event;
import org.netcracker.eventteammatessearch.entity.mongoDB.Review;
import org.netcracker.eventteammatessearch.persistence.repositories.EventRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.mongo.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ReviewService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    @PreAuthorize("#review.id.userId eq #principal.name")
    public void addReview(Review review, Principal principal) {
        Optional<Event> eventOptional = eventRepository.findById(review.getId().getEventId());
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            if (event.getGuests().stream().anyMatch(e -> e.getUser().getLogin().equals(principal.getName()))) {
                review.setEventOwnerId(event.getOwner().getLogin());
                reviewRepository.save(review);
            } else throw new ObjectNotFoundException(principal.getName(), "Event");
        } else throw new ObjectNotFoundException(review.getId().getEventId(), "Event");
    }

    public void setMarksToEvents(List<Event> eventList) {
        List<String> logins = eventList.stream().map(e -> e.getOwner().getLogin()).collect(Collectors.toList());
        Map<String, Double> usersRatingMap = reviewRepository.getReviewsByEventOwnerIdIsIn(logins).stream().collect(Collectors.toMap(e -> e.getEventOwnerId(), e -> e.getMark()));
        eventList.forEach(event -> {
            if (usersRatingMap.containsKey(event.getOwner().getLogin())) {
                event.setAvgMark(Math.floor(usersRatingMap.get(event.getOwner().getLogin())));
            }
        });
    }
}
