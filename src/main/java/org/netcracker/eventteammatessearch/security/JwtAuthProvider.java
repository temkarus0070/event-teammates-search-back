package org.netcracker.eventteammatessearch.security;

import org.netcracker.eventteammatessearch.security.Entity.JWTUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthProvider implements AuthenticationProvider {
    @Value("${secretKey}")
    private String secretKey;


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
        return JWTUser.class.isAssignableFrom(aClass);
    }
}
