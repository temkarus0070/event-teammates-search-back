package org.netcracker.eventteammatessearch.controllers;

import org.netcracker.eventteammatessearch.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.netcracker.eventteammatessearch.entity.User;

import java.security.Principal;
import java.util.List;

@RestController

public class UsersController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/api/usersList")
    public List<User> get(String login, Principal principal) {
        System.out.println("\n\n\n" + userRepository.getUsersByLogin(login) + "\n\n\n");
        return userRepository.getUsersByLogin(login);
        //userRepository.findBy()
    }
}
