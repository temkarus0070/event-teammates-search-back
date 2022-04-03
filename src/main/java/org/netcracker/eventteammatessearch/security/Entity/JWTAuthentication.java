package org.netcracker.eventteammatessearch.security.Entity;

import io.jsonwebtoken.Jwts;
import org.netcracker.eventteammatessearch.security.Persistence.Entity.UserDetailsManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JWTAuthentication implements Authentication {
    private String jwt;
    private io.jsonwebtoken.JwtParser parser;
    private boolean isAuthenticated;
    private String refreshToken;
    private UserDetailsManager userDetailsManager;
    private String username;

    public JWTAuthentication(String jwt, UserDetailsManager userDetailsManager) {
        this.jwt = jwt;
        this.userDetailsManager = userDetailsManager;
    }


    public JWTAuthentication(String jwt, String secretKey, String refreshToken, UserDetailsManager userDetailsManager) {
        this.jwt = jwt;
        this.parser = Jwts.parser().setSigningKey(secretKey);
        this.refreshToken = refreshToken;
        this.isAuthenticated = true;
        this.userDetailsManager = userDetailsManager;
    }

    public JWTAuthentication(String jwt, String secretKey, UserDetailsManager userDetailsManager) {
        this.jwt = jwt;
        this.parser = Jwts.parser().setSigningKey(secretKey);
        this.userDetailsManager = userDetailsManager;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userDetailsManager.loadUserByUsername(getPrincipal().toString()).getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return jwt;
    }

    @Override
    public Object getDetails() {
        return refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    @Override
    public Object getPrincipal() {
        if (username == null)
            username = String.valueOf(parser.parseClaimsJws(jwt).getBody().get("username"));
        return username;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.isAuthenticated = isAuthenticated;
    }

    @Override
    public boolean equals(Object another) {
        return false;
    }

    @Override
    public String toString() {
        return jwt;
    }

    @Override
    public int hashCode() {
        return jwt.hashCode();
    }

    @Override
    public String getName() {
        return String.valueOf(getPrincipal());
    }
}
