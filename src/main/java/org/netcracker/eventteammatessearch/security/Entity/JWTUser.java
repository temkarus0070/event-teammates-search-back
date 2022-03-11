package org.netcracker.eventteammatessearch.security.Entity;

import io.jsonwebtoken.Jwts;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

public class JWTUser implements UserDetails {
    private String jwt;
    private io.jsonwebtoken.JwtParser parser;
    private LocalDateTime expire;

    public JWTUser(String jwt, String signKey) {
        this.jwt = jwt;
        parser = Jwts.parser().setSigningKey(signKey);
        expire = (LocalDateTime) parser.parseClaimsJws(jwt).getBody().get("expire");
    }

    public JWTUser(JwtUserEntity jwtUserEntity, String signKey) {
        this.jwt = jwtUserEntity.getJwtUserKey().getJwt();
        parser = Jwts.parser().setSigningKey(signKey);
        expire = (LocalDateTime) parser.parseClaimsJws(jwt).getBody().get("expire");
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return (Collection<? extends GrantedAuthority>) parser.parseClaimsJws(jwt).getBody().get("authorities");
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return String.valueOf(parser.parseClaimsJws(jwt).getBody().get("username"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return expire.isAfter(LocalDateTime.now());
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return expire.isAfter(LocalDateTime.now());
    }

    @Override
    public boolean isEnabled() {
        return expire.isAfter(LocalDateTime.now());
    }
}
