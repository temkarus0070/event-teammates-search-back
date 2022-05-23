package org.netcracker.eventteammatessearch.Services;

import org.netcracker.eventteammatessearch.entity.mongoDB.Notification;
import org.netcracker.eventteammatessearch.persistence.repositories.mongo.NotificationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationsRepository notificationsRepository;

    @Autowired
    private EventsService eventsService;

    public List<Notification> checkNotifications(Principal principal) {
        return notificationsRepository.findAllByUserIdOrderByIsShownAsc(principal.getName());
    }

    public boolean hasNew(Principal principal) {
        if (eventsService.getInvitesEvent(principal.getName()).size() != 0) return true;
        List<Notification> allByUserIdAndShownTrue = notificationsRepository.findAllByUserIdAndIsShownIsFalse(principal.getName());
        if (allByUserIdAndShownTrue.size() > 0)
            return true;
        return false;

    }

    public void setAsShown(List<Notification> notifications) {
        notifications.forEach(e -> e.setShown(true));
        notificationsRepository.saveAll(notifications);
    }
}
