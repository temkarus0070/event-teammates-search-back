package org.netcracker.eventteammatessearch.security;


import org.netcracker.eventteammatessearch.entity.User;
import org.netcracker.eventteammatessearch.security.Entity.UserDetails;
import org.netcracker.eventteammatessearch.security.Persistence.Entity.UserDetailsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RegistrationController {
    @Autowired
    private UserDetailsManager userDetailsManager;

    @PostMapping("/register")
    public void register(@RequestBody User user) {
        user.setAuthorities(List.of(new SimpleGrantedAuthority("user")));
        userDetailsManager.createUser(new UserDetails(user));
    }


    @PostMapping("/login")
    public void login() {
    }

}
