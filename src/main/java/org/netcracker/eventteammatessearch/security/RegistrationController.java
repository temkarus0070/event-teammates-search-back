package org.netcracker.eventteammatessearch.security;


import org.netcracker.eventteammatessearch.entity.User;
import org.netcracker.eventteammatessearch.security.Entity.UserDetails;
import org.netcracker.eventteammatessearch.security.Persistence.Entity.JwtUserEntity;
import org.netcracker.eventteammatessearch.security.Persistence.Entity.UserDetailsManager;
import org.netcracker.eventteammatessearch.security.Services.JwtTokenGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class RegistrationController {
    @Autowired
    private UserDetailsManager userDetailsManager;

    @Autowired
    private JwtTokenGeneratorService jwtTokenGeneratorService;

    @PostMapping("/register")
    public void register(@RequestBody User user) {
        user.setAuthorities(List.of(new SimpleGrantedAuthority("user")));
        user.setRegistrationDate(LocalDate.now());
        userDetailsManager.createUser(new UserDetails(user));

    }


    @PostMapping("/login")
    public void login() {
    }

    @PostMapping("/refreshToken")
    public JwtUserEntity refreshToken(@RequestBody JwtUserEntity jwtUser) {
        return jwtTokenGeneratorService.refreshToken(jwtUser);
    }

}
