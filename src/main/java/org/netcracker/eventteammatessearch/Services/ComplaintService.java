package org.netcracker.eventteammatessearch.Services;

import org.netcracker.eventteammatessearch.entity.*;
import org.netcracker.eventteammatessearch.entity.mongoDB.Notification;
import org.netcracker.eventteammatessearch.entity.mongoDB.sequenceGenerators.SequenceGeneratorService;
import org.netcracker.eventteammatessearch.persistence.repositories.ComplaintRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.EventRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.UserRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.mongo.NotificationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ComplaintService {
    private static Random random = new Random(new Date().getTime());
    @Autowired
    private ComplaintRepository complaintRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationsRepository notificationsRepository;

    @Autowired
    private EventsService eventsService;

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    @Autowired
    private EventRepository eventRepository;

    public void create(Complaint complaint) {
        Optional<User> optionalUser = userRepository.findById(complaint.getId().getUserId());
        optionalUser.ifPresent(u -> {
            if (u.isBlocked()) {
                throw new AccessDeniedException("Данная возможность заблокирована для вашего аккаунта");
            }
            Event event = new Event();
            event.setId(complaint.getId().getEventId());
            complaint.setEvent(event);
            complaint.setUser(u);
            setComplaintResolver(complaint);
            this.complaintRepository.save(complaint);
        });
    }

    private void setComplaintResolver(Complaint complaint) {
        List<User> usersByAuthoritiesContains = userRepository.findUsersByAuthorities("complaint Resolver");
        int index = random.nextInt(usersByAuthoritiesContains.size());
        complaint.setComplaintResolver(usersByAuthoritiesContains.get(index));
    }

    public List<Complaint> findComplaintsAssignedToMe(Authentication authentication) {
        List<Complaint> allByComplaintResolver_login = complaintRepository.findAllByComplaintResolver_Login(authentication.getName());
        return allByComplaintResolver_login;
    }

    public void sendWarningToEventOwner(long eventId, String text) {
        Event event = eventsService.get(eventId);
        if (event != null) {
            Notification notification = new Notification();
            notification.setId(sequenceGeneratorService.generateSequence(Notification.SEQUENCE_NAME));
            notification.setDescription(text);
            notification.setUserId(event.getOwner().getLogin());
            notification.setTitle(String.format("Вам вынесено предупреждение за событие %s", event.getName()));
            notificationsRepository.save(notification);
        }
    }

    public void closeComplaint(String userId, long eventId) {
        Optional<Complaint> byId = complaintRepository.findById(new UserEventKey(eventId, userId));
        byId.ifPresent(e -> {
            e.setStatus(Status.ACCEPTED);
            complaintRepository.save(e);
        });
    }

    public void rejectComplaint(String userId, long eventId) {
        Optional<Complaint> byId = complaintRepository.findById(new UserEventKey(eventId, userId));
        byId.ifPresent(e -> {
            e.setStatus(Status.REJECTED);
            complaintRepository.save(e);
        });
    }

    public void banUserByComplaint(long eventId) {
        Event event = eventsService.get(eventId);
        if (event != null) {
            Notification notification = new Notification();
            notification.setId(sequenceGeneratorService.generateSequence(Notification.SEQUENCE_NAME));
            notification.setTitle("Вы были заблокированы по результатам рассмотрения жалоб на ваши события");
            notification.setUserId(event.getOwner().getLogin());
            notification.setDescription("Теперь вы не можете создавать новые события и отправлять жалобы");
            notificationsRepository.save(notification);
            User owner = event.getOwner();
            owner.setBlocked(true);

            userRepository.save(owner);
        }
    }

    public void banUserForInvalidComplaints(String userId) {
        Optional<User> user = userRepository.findById(userId);
        user.ifPresent(u -> {
            Notification notification = new Notification();
            notification.setId(sequenceGeneratorService.generateSequence(Notification.SEQUENCE_NAME));
            notification.setTitle("Вы были заблокированы за спам жалобами");
            notification.setUserId(userId);
            notification.setDescription("Теперь вы не можете создавать новые события и отправлять жалобы");
            notificationsRepository.save(notification);
            u.setBlocked(true);
            userRepository.save(u);
        });
    }

    public void banEvent(String userId, long eventId) {
        Event event = eventsService.get(eventId);
        if (event != null) {
            event.setHidden(true);
            List<Notification> notifications = new ArrayList<>();
            event.getGuests().forEach(g -> {
                Notification notification = new Notification();
                notification.setId(sequenceGeneratorService.generateSequence(Notification.SEQUENCE_NAME));
                notification.setTitle(String.format("Событие %s заблокировано", event.getName()));
                notification.setUserId(g.getId().getUserId());
                notification.setDescription("Ваше участие аннулировано");
                notifications.add(notification);
            });
            Notification notification = new Notification();
            notification.setId(sequenceGeneratorService.generateSequence(Notification.SEQUENCE_NAME));
            notification.setTitle(String.format("Событие %s заблокировано", event.getName()));
            notification.setUserId(event.getOwner().getLogin());
            notification.setDescription("Ваше событие заблокировано");
            notifications.add(notification);
            event.setGuests(new HashSet<>());
            eventRepository.save(event);
            notificationsRepository.saveAll(notifications);
        }
    }
}
