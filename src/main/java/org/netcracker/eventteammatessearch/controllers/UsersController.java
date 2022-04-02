package org.netcracker.eventteammatessearch.controllers;

import org.netcracker.eventteammatessearch.Services.UserService;
import org.netcracker.eventteammatessearch.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/getUserByLogin")
    public User getByLogin(@RequestParam String userLogin) {
        return userService.getUserByLogin(userLogin);
    }

    @PatchMapping("/updateUser")
    public void updateUser(@RequestBody User user) {
        userService.updateUser(user);
    }

}
