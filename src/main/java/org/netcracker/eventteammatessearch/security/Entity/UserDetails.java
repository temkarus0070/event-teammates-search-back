package org.netcracker.eventteammatessearch.security.Entity;

import org.netcracker.eventteammatessearch.entity.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserDetails implements org.springframework.security.core.userdetails.UserDetails {
    private User user;
    private List<GrantedAuthority> grantedAuthorityList=new ArrayList<>();

    public UserDetails(User user) {
        this.user = user;
        if (user.getAuthorities()!=null)
        this.grantedAuthorityList=user.getAuthorities().stream().map(e->(GrantedAuthority)()->e).collect(Collectors.toList());
    }


    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorityList;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getLogin();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
