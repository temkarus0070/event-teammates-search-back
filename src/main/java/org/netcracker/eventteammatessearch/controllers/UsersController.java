package org.netcracker.eventteammatessearch.controllers;

import org.netcracker.eventteammatessearch.entity.User;
import org.netcracker.eventteammatessearch.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@RestController

public class UsersController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/api/usersListByLogin")
    public List<User> getUsersListByLogin(HttpServletRequest request, Principal principal) {
        return userRepository.getUsersByLogin(request.getHeader("login"));
    }

    @GetMapping("/api/usersListByName")
    public List<User> getUsersListByName(HttpServletRequest request, Principal principal) {
        return userRepository.getUsersByName(request.getHeader("firstName"), request.getHeader("lastName"));
    }
}
