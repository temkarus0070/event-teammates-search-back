package org.netcracker.eventteammatessearch.controllers;

import org.netcracker.eventteammatessearch.entity.Relationship;
import org.netcracker.eventteammatessearch.entity.User;
import org.netcracker.eventteammatessearch.persistence.repositories.RelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

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
        relationship.setFriend(false);
        /*
        System.out.println("\n\n\n");
        System.out.println(relationshipRepository.getRelationshipByUsersLogin(owner, friend));
        System.out.println("\n\n\n");

         */
        if (relationshipRepository.getRelationshipByUsersLogin(owner, friend) == null)
            relationshipRepository.save(relationship);
        /*
        System.out.println("\n\n\n\n\n");
        System.out.println("owner: " + relationship.getOwner());
        System.out.println("friend: " + relationship.getFriend());
        System.out.println("isFriend: " + relationship.getIsFriend());
        System.out.println("\n\n\n\n\n");
         */
        return relationship;
    }
}
