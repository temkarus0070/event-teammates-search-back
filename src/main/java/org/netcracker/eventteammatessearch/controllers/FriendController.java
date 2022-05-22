package org.netcracker.eventteammatessearch.controllers;

import org.netcracker.eventteammatessearch.entity.Relationship;
import org.netcracker.eventteammatessearch.entity.User;
import org.netcracker.eventteammatessearch.persistence.repositories.RelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@RequestMapping("/api/friends")
@RestController
public class FriendController {

    @Autowired
    private RelationshipRepository relationshipRepository;

    //------------------------------------------------------------------ post finctions

    @PostMapping("/requestFriend")
    public Relationship requestFriend(HttpServletRequest request, Principal principal) {
        Relationship relationship = new Relationship();
        User owner = new User();
        String ownerLogin = principal.getName();
        owner.setLogin(ownerLogin);
        User friend = new User();
        String friendLogin = request.getHeader("friendName");
        friend.setLogin(friendLogin);
        Relationship.RelationshipId relationshipId = new Relationship.RelationshipId(friend.getLogin(),owner.getLogin());
        relationship.setId(relationshipId);
        relationship.setFriend(friend);
        relationship.setOwner(owner);
        if (request.getHeader("friend") == null) {
            relationship.setItFriend(false);
            if (relationshipRepository.getRelationshipByUsersLogin(owner.getLogin(), friend.getLogin()) == null)
                relationshipRepository.save(relationship);
        } else {
            relationship.setItFriend(true);
            if (relationshipRepository.getRelationshipByUsersLogin(owner.getLogin(), friend.getLogin()) == null)
                relationshipRepository.save(relationship);
            Relationship temp = relationshipRepository.getRelationshipByUsersLogin(friend.getLogin(), owner.getLogin());
            temp.setItFriend(true);
            relationshipRepository.save(temp);
        }
        return relationship;

    }

    @PostMapping("/cancelFriend")
    public void deleteFriendRequest(HttpServletRequest request, Principal principal) {
        User owner = new User();
        owner.setLogin(principal.getName());
        User friend = new User();
        friend.setLogin(request.getHeader("friendName"));
        relationshipRepository.delete(relationshipRepository.getRelationshipByUsersLogin(friend.getLogin(), owner.getLogin()));
    }

    @PostMapping("/deleteFriend")
    public void deleteFriend(HttpServletRequest request, Principal principal) {
        User owner = new User();
        owner.setLogin(principal.getName());
        User friend = new User();
        friend.setLogin(request.getHeader("friendName"));
        relationshipRepository.delete(relationshipRepository.getRelationshipByUsersLogin(owner.getLogin(), friend.getLogin()));
        Relationship temp = relationshipRepository.getRelationshipByUsersLogin(friend.getLogin(), owner.getLogin());
        relationshipRepository.delete(temp);
        temp.setItFriend(false);
        relationshipRepository.save(temp);
    }

    @PostMapping("/deleteSendedRequest")
    public void deleteSendedRequest(HttpServletRequest request, Principal principal) {
        User owner = new User();
        owner.setLogin(principal.getName());
        User friend = new User();
        friend.setLogin(request.getHeader("friendName"));
        relationshipRepository.delete(relationshipRepository.getRelationshipByUsersLogin(owner.getLogin(), friend.getLogin()));
    }

    //------------------------------------------------------------------ get functions

    @GetMapping("/getRequests")
    public List<Relationship> getRequests(Principal principal) {
        User owner = new User();
        owner.setLogin(principal.getName());
        return cleanFromEntities(relationshipRepository.getRelationshipsRequestsByOwner(owner.getLogin()));
    }

    @GetMapping("/getFriends")
    public List<Relationship> getFriends(Principal principal) {
        User owner = new User();
        owner.setLogin(principal.getName());
        return cleanFromEntities(relationshipRepository.getRelationshipsFriendsByOwner(owner.getLogin()));
    }

    @GetMapping("/getSendedRequests")
    public List<Relationship> getSendedRequests(Principal principal) {
        User owner = new User();
        owner.setLogin(principal.getName());
        return cleanFromEntities(relationshipRepository.getRelationshipSendedRequestsByOwner(owner.getLogin()));
    }

    private List<Relationship> cleanFromEntities(List<Relationship> relationships){
        if (relationships!=null){
            relationships.forEach(r->{
                if (r.getFriend()!=null)
                    r.setFriend(new User(r.getFriend().getLogin(),r.getFriend().getFirstName(),r.getFriend().getLastName(),r.getFriend().getPictureUrl()));
                if (r.getOwner()!=null)
                    r.setOwner(new User(r.getOwner().getLogin(),r.getOwner().getFirstName(),r.getOwner().getLastName(),r.getOwner().getPictureUrl()));
            });
        }
        return relationships;
    }

}
