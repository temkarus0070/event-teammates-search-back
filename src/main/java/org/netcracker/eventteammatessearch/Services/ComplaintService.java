package org.netcracker.eventteammatessearch.Services;

import org.netcracker.eventteammatessearch.entity.Complaint;
import org.netcracker.eventteammatessearch.entity.User;
import org.netcracker.eventteammatessearch.persistence.repositories.ComplaintRepository;
import org.netcracker.eventteammatessearch.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class ComplaintService {
    private static Random random=new Random(new Date().getTime());
    @Autowired
private ComplaintRepository complaintRepository;
@Autowired
    private UserRepository userRepository;

    public void create(Complaint complaint){
        setComplaintResolver(complaint);
        this.complaintRepository.save(complaint);
    }

    private void setComplaintResolver(Complaint complaint){
        GrantedAuthority complaintGrantedAuthority=()->"complaint Resolver";
        List<User> usersByAuthoritiesContains = userRepository.findUsersByAuthoritiesContains(complaintGrantedAuthority);
        int index = random.nextInt(usersByAuthoritiesContains.size());
        complaint.setComplaintResolver(usersByAuthoritiesContains.get(index));
    }

    public List<Complaint> findComplaintsAssignedToMe(Authentication authentication){
        return complaintRepository.findAllByComplaintResolver_Login(authentication.getName());
    }
}
