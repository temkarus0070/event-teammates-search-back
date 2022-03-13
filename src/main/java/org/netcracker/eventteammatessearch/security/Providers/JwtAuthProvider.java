package org.netcracker.eventteammatessearch.security.Providers;

import org.netcracker.eventteammatessearch.security.Entity.JWTAuthentication;
import org.netcracker.eventteammatessearch.security.Persistence.Entity.JWTUser;
import org.netcracker.eventteammatessearch.security.Persistence.JwtTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthProvider implements AuthenticationProvider {
    @Value("${jwt.secretKey}")
    private String secretKey;

    private JwtTokenRepository jwtTokenRepository;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            JWTUser jwtUser = new JWTUser(authentication.getCredentials().toString(), secretKey);
            if (jwtUser.isAccountNonExpired()) {
                authentication.setAuthenticated(true);
                return authentication;
            }

        } catch (Exception exception) {
            throw new BadCredentialsException(exception.getMessage());
        }

        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return JWTAuthentication.class.isAssignableFrom(aClass);
    }
}
