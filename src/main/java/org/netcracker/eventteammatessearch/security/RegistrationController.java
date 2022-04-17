package org.netcracker.eventteammatessearch.security;


import org.netcracker.eventteammatessearch.entity.User;
import org.netcracker.eventteammatessearch.security.Entity.UserDetails;
import org.netcracker.eventteammatessearch.security.Persistence.Entity.JwtUserEntity;
import org.netcracker.eventteammatessearch.security.Persistence.Entity.UserDetailsManager;
import org.netcracker.eventteammatessearch.security.Persistence.JwtTokenRepository;
import org.netcracker.eventteammatessearch.security.Services.JwtTokenGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private JwtTokenRepository jwtTokenRepository;

    @PostMapping("/register")
    public void register(@RequestBody User user) {
        user.setAuthorities(List.of(new SimpleGrantedAuthority("user")));
        user.setRegistrationDate(LocalDate.now());
        userDetailsManager.createUser(new UserDetails(user));

    }


    @PostMapping("/login")
    public void login() {
    }

    @PostMapping("/api/generateRefreshToken")
    public JwtUserEntity getRefreshToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String jwtToken = authentication.getCredentials().toString();
        String refreshToken = jwtTokenGeneratorService.generateRefresh();
        JwtUserEntity save = jwtTokenRepository.save(new JwtUserEntity(new JwtUserEntity.JwtUserKey(authentication.getName(), jwtToken), refreshToken));
        return save;
    }


    @PostMapping("/refreshToken")
    public JwtUserEntity refreshToken(@RequestBody JwtUserEntity jwtUser) throws TokenNotFoundException {
        JwtUserEntity jwtUserEntity = jwtTokenGeneratorService.refreshToken(jwtUser);
        if (jwtUserEntity == null) {
            throw new TokenNotFoundException();
        }
        return jwtUserEntity;
    }

}
