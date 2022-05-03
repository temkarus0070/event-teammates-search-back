package org.netcracker.eventteammatessearch.controllers;

import org.netcracker.eventteammatessearch.Services.ComplaintService;
import org.netcracker.eventteammatessearch.entity.Complaint;
import org.netcracker.eventteammatessearch.entity.mongoDB.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/complaint")
@PreAuthorize("hasAuthority('complaint Resolver')")
public class ComplaintController {
    @Autowired
    private ComplaintService complaintService;

    @PreAuthorize("permitAll() && #complaint.id.userId eq #principal.name")
    @PostMapping("/create")

    public void create(@RequestBody Complaint complaint, Principal principal) {
        try {
            complaint.getId().setUserId(principal.getName());
            complaintService.create(complaint);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }


    @GetMapping("getAssignedComplaints")
    public List<Complaint> getComplaintsAssignedToMe(Authentication authentication) {
        return complaintService.findComplaintsAssignedToMe(authentication);
    }


    @PostMapping("/banComplaintAuthor")

    public void banComplaintAuthor(@RequestParam long eventId, @RequestParam String complaintAuthor, Principal principal) {
        complaintService.banUserForInvalidComplaints(complaintAuthor);
        complaintService.closeComplaint(complaintAuthor, eventId);
    }

    @PostMapping("/banEventOwner")
    public void banEventOwner(@RequestParam long eventId, @RequestParam String complaintAuthor) {
        complaintService.banUserByComplaint(eventId);
        complaintService.closeComplaint(complaintAuthor, eventId);
    }

    @PostMapping("/rejectComplaint")
    public void rejectComplaint(@RequestParam long eventId, @RequestParam String complaintAuthor) {
        complaintService.rejectComplaint(complaintAuthor, eventId);
    }


    @PostMapping("/banEvent")
    public void banEvent(@RequestParam long eventId, @RequestParam String complaintAuthor) {
        complaintService.banEvent(complaintAuthor, eventId);
        complaintService.closeComplaint(complaintAuthor, eventId);
    }

    @PostMapping("/sendWarningToEventOwner")
    public void sendWarningToEventOwner(@RequestBody Message message, @RequestParam long eventId, @RequestParam String complaintAuthor) {
        complaintService.sendWarningToEventOwner(eventId, message.getText());
        complaintService.closeComplaint(complaintAuthor, eventId);
    }
}
