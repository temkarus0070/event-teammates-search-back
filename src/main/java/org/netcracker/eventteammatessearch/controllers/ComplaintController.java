package org.netcracker.eventteammatessearch.controllers;

import org.netcracker.eventteammatessearch.Services.ComplaintService;
import org.netcracker.eventteammatessearch.entity.Complaint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/complaint")
public class ComplaintController {
    @Autowired
    private ComplaintService complaintService;
    @PostMapping("/create")
    public void create(@RequestBody Complaint complaint){
complaintService.create(complaint);
    }

    @PreAuthorize("#authentication.getAuthorities().?[#this.getAuthority() eq 'complaint Resolver'].size()>0")
    @GetMapping("getAssignedComplaints")
    public List<Complaint> getComplaintsAssignedToMe(Authentication authentication){
return complaintService.findComplaintsAssignedToMe(authentication);
    }
}
