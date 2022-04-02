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

    @GetMapping("/api/usersList")
    public List<User> get(HttpServletRequest request, Principal principal) {
        List<User> login = userRepository.getUsersByLogin(request.getHeader("login"));
        return login;
    }
}
