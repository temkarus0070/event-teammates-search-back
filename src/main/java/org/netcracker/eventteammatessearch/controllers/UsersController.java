package org.netcracker.eventteammatessearch.controllers;

import org.netcracker.eventteammatessearch.Services.UserService;
import org.netcracker.eventteammatessearch.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RequestMapping(value = "/api/users")
@RestController
public class UsersController {

    @Autowired
    UserService userService;

    @GetMapping("/getUsers")
    public List<User> getUsers() {
        return userService.getAll();
    }

    @GetMapping("/getUserByToken")
    public User getByToken(@RequestParam String userToken) {
        return userService.getUserByToken(userToken);
    }

    @GetMapping("/getUserByLogin")
    public Optional<User> getByLogin(@RequestParam String userLogin) {
        return userService.getUserByLogin(userLogin);
    }

}
