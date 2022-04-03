package org.netcracker.eventteammatessearch.controllers;

import org.netcracker.eventteammatessearch.entity.Relationship;
import org.netcracker.eventteammatessearch.entity.User;
import org.netcracker.eventteammatessearch.persistence.repositories.RelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@RestController
public class FriendController {

    @Autowired
    private RelationshipRepository relationshipRepository;

    @PostMapping("/api/requestFriend")
    public Relationship requestFriend(HttpServletRequest request, Principal principal) {
        Relationship relationship = new Relationship();
        User owner = new User();
        String ownerLogin = principal.getName();
        owner.setLogin(ownerLogin);
        User friend = new User();
        String friendLogin = request.getHeader("friendName");
        friend.setLogin(friendLogin);
        Relationship.RelationshipId relationshipId = new Relationship.RelationshipId(owner, friend);
        relationship.setId(relationshipId);
        if (request.getHeader("friend") == null) {
            relationship.setFriend(false);
            if (relationshipRepository.getRelationshipByUsersLogin(owner, friend) == null)
                relationshipRepository.save(relationship);
        } else {
            relationship.setFriend(true);
            if (relationshipRepository.getRelationshipByUsersLogin(owner, friend) == null)
                relationshipRepository.save(relationship);
            Relationship temp = relationshipRepository.getRelationshipByUsersLogin(friend, owner);
            temp.setFriend(true);
            relationshipRepository.delete(temp);
            relationshipRepository.save(temp);
        }
        return relationship;

    }

    @GetMapping("/api/getRequests")
    public List<Relationship> getRequests(Principal principal) {
        User owner = new User();
        owner.setLogin(principal.getName());
        return relationshipRepository.getRelationshipsRequestsByFriend(owner);
    }

    @PostMapping("/api/cancelFriend")
    public void deleteFriendRequest(HttpServletRequest request, Principal principal) {
        User owner = new User();
        owner.setLogin(principal.getName());
        User friend = new User();
        friend.setLogin(request.getHeader("friendName"));
        relationshipRepository.delete(relationshipRepository.getRelationshipByUsersLogin(friend, owner));
    }

}
