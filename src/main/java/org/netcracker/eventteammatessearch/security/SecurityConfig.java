package org.netcracker.eventteammatessearch.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private JwtAuthProvider jwtAuthProvider;
    private JWTFilter jwtFilter;


    public SecurityConfig(JwtAuthProvider jwtAuthProvider, @Lazy JWTFilter jwtFilter) {
        this.jwtAuthProvider = jwtAuthProvider;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable()
                .cors().disable();
        http.authenticationProvider(jwtAuthProvider)
                .authorizeRequests()
                .anyRequest()
                .authenticated();
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }


}
