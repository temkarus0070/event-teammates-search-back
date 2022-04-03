package org.netcracker.eventteammatessearch.security.Persistence.Entity;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

public class JWTUser implements UserDetails {
    private String jwt;
    private io.jsonwebtoken.JwtParser parser;
    private LocalDateTime expire;
    private boolean isLocked;
    private boolean isExpire;

    public JWTUser(String jwt, String signKey) {
        this.jwt = jwt;
        parser = Jwts.parser().setSigningKey(signKey);
        try {
            parser.parseClaimsJws(jwt);
        } catch (ExpiredJwtException e) {
            isExpire = true;
        }
    }

    public JWTUser(JwtUserEntity jwtUserEntity, String signKey) {
        this.jwt = jwtUserEntity.getId().getJwt();
        parser = Jwts.parser().setSigningKey(signKey);
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
        return !isExpire;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isExpire;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !isExpire;
    }

    @Override
    public boolean isEnabled() {
        return !isExpire;
    }
}
