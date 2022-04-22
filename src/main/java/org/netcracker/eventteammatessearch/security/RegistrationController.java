package org.netcracker.eventteammatessearch.security;


import org.hibernate.HibernateException;
import org.netcracker.eventteammatessearch.entity.User;
import org.netcracker.eventteammatessearch.security.Entity.UserDetails;
import org.netcracker.eventteammatessearch.security.Persistence.Entity.JwtUserEntity;
import org.netcracker.eventteammatessearch.security.Persistence.Entity.UserDetailsManager;
import org.netcracker.eventteammatessearch.security.Persistence.JwtTokenRepository;
import org.netcracker.eventteammatessearch.security.Services.AuthService;
import org.netcracker.eventteammatessearch.security.Services.JwtTokenGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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

    @Autowired
    private AuthService authService;

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
        JwtUserEntity save = jwtTokenRepository.save(new JwtUserEntity(new JwtUserEntity.JwtUserKey(authentication.getName(), jwtToken), refreshToken,null));
        return save;
    }


    @PostMapping("/completeOauth")
    public JwtUserEntity completeOauth(@RequestBody JwtUserEntity jwtUser) {
        try {
            return authService.register(jwtUser);
        }
        catch (Exception exception){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(),exception);
        }
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
