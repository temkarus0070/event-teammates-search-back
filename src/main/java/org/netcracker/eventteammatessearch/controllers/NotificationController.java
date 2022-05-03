package org.netcracker.eventteammatessearch.controllers;

import org.netcracker.eventteammatessearch.Services.NotificationService;
import org.netcracker.eventteammatessearch.entity.mongoDB.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/getAll")
    public List<Notification> getAll(Principal principal) {
        return notificationService.checkNotifications(principal);
    }

    @GetMapping("/hasNew")
    public boolean hasNew(Principal principal) {
        return notificationService.hasNew(principal);
    }

    @PostMapping("/setAsShown")
    public void setAsShown(@RequestBody List<Notification> notifications) {
        notificationService.setAsShown(notifications);
    }
}
