package org.netcracker.eventteammatessearch.security.Providers;

import org.netcracker.eventteammatessearch.security.Persistence.Entity.UserDetailsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class UsernamePasswordAuthProvider implements AuthenticationProvider {
    private static final Logger logger = LoggerFactory.getLogger(UsernamePasswordAuthProvider.class);
    @Autowired
    private UserDetailsManager userDetailsManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            UserDetails userDetails = userDetailsManager.loadUserByUsername(authentication.getName());
        if (userDetails != null && passwordEncoder.matches(String.valueOf(authentication.getCredentials()), userDetails.getPassword())) {
            return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
        } else {
            logger.error(String.format("User %s entered invalid password or login", authentication.getName()));
            throw new BadCredentialsException("YOUR LOGIN OR PASS are invalid");

        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
