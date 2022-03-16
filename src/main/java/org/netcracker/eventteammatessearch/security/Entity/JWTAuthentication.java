package org.netcracker.eventteammatessearch.security.Entity;

import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JWTAuthentication implements Authentication {
    private String jwt;
    private io.jsonwebtoken.JwtParser parser;
    private boolean isAuthenticated;
    private String refreshToken;

    public JWTAuthentication(String jwt, String secretKey, String refreshToken) {
        this.jwt = jwt;
        this.parser = Jwts.parser().setSigningKey(secretKey);
        this.refreshToken = refreshToken;
        this.isAuthenticated = true;
    }

    public JWTAuthentication(String jwt, String secretKey) {
        this.jwt = jwt;
        this.parser = Jwts.parser().setSigningKey(secretKey);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return (Collection<? extends GrantedAuthority>) parser.parseClaimsJws(jwt).getBody().get("authorities");
    }

    @Override
    public Object getCredentials() {
        return jwt;
    }

    @Override
    public Object getDetails() {
        return refreshToken;
    }

    @Override
    public Object getPrincipal() {
        return String.valueOf(parser.parseClaimsJws(jwt).getBody().get("username"));
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
