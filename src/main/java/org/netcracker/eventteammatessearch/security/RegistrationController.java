package org.netcracker.eventteammatessearch.security;


import org.netcracker.eventteammatessearch.security.Entity.UserDetails;
import org.netcracker.eventteammatessearch.security.Persistence.Entity.UserDetailsManager;
import org.netcracker.eventteammatessearch.security.Persistence.Entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RegistrationController {
    @Autowired
    private UserDetailsManager userDetailsManager;

    @PostMapping("/register")
    public void register(String username, String password) {
        userDetailsManager.createUser(new UserDetails(new UserEntity(username, password, List.of(new SimpleGrantedAuthority("user")))));

    }


}
