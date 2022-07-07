package org.netcracker.eventteammatessearch.dto;

import lombok.Getter;
import lombok.Setter;
import org.netcracker.eventteammatessearch.entity.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class EventDto {

    private Long id;
    private String theme;
    private String name;
    private EventType eventType;
    private String description;
    private LocalDateTime dateTimeStart;
    private LocalDateTime dateTimeEnd;
    private int maxNumberOfGuests;
    private float price;
    private boolean hasChatWithOwner;
    private boolean isPrivate;
    private boolean isOnline;
    private boolean isHidden;
    private Chat chat;
    private String url;
    private double avgMark;
    private boolean currentUserInvited;
    private boolean currentUserEntered;
    private Location location;
    private User owner;
    private Set<EventAttendance> guests;
    private boolean recommendedBySurvey;
    private long visitorsCount;
    private Set<String> tags;

    public EventDto(Event e) {
        Chat chat = null;
        Location location = null;
        if (e.getLocation() != null) {
            location = new Location(e.getLocation().getName(), e.getLocation().getLocation());
        }
        if (e.getChat() != null) {
            chat = new Chat();
            chat.setId(e.getChat().getId());
        }
        User owner = null;
        if (e.getOwner() != null) {
            owner = new User(e.getOwner().getLogin(), e.getOwner().isPhoneConfirmed());
        }
        Set<EventAttendance> guests = new HashSet<>();
        if (e.getGuests() != null) {
            guests = e.getGuests().stream().map(u -> new EventAttendance(u.getId().getUserId())).collect(Collectors.toSet());
        }
        this.id = e.getId();
        this.theme = e.getTheme();
        this.eventType = e.getEventType();
        this.name = e.getName();
        this.description = e.getDescription();
        this.dateTimeStart = e.getDateTimeStart();
        this.dateTimeEnd = e.getDateTimeEnd();
        this.maxNumberOfGuests = e.getMaxNumberOfGuests();
        this.price = e.getPrice();
        this.hasChatWithOwner = e.isHasChatWithOwner();
        this.isPrivate = e.isPrivate();
        this.isOnline = e.isOnline();
        this.isHidden = e.isHidden();
        this.chat = e.getChat();
        this.url = e.getUrl();
        this.avgMark = e.getAvgMark();
        this.currentUserEntered = e.isCurrentUserEntered();
        this.currentUserInvited = e.isCurrentUserInvited();
        this.location = location;
        this.owner = owner;
        this.guests = guests;
        this.recommendedBySurvey = e.isRecommendedBySurvey();
        this.visitorsCount = e.getVisitorsCount();
        this.tags = e.getTags();
    }
}
