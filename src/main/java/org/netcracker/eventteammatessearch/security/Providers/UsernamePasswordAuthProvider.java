package org.netcracker.eventteammatessearch.security.Providers;

import org.netcracker.eventteammatessearch.security.Persistence.Entity.UserDetailsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UsernamePasswordAuthProvider implements AuthenticationProvider {
    @Autowired
    private UserDetailsManager userDetailsManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            UserDetails userDetails = userDetailsManager.loadUserByUsername(authentication.getName());
            if (passwordEncoder.matches(String.valueOf(authentication.getCredentials()), userDetails.getPassword())) {
                return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
            }
        } catch (Exception ex) {
            return authentication;
        }
        return null;

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
