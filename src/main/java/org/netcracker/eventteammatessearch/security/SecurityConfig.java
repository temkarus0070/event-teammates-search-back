package org.netcracker.eventteammatessearch.security;

import org.netcracker.eventteammatessearch.security.Filters.JWTFilter;
import org.netcracker.eventteammatessearch.security.Filters.UsernamePasswordFilter;
import org.netcracker.eventteammatessearch.security.Providers.JwtAuthProvider;
import org.netcracker.eventteammatessearch.security.Providers.UsernamePasswordAuthProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity()
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private JwtAuthProvider jwtAuthProvider;
    private JWTFilter jwtFilter;
    private UsernamePasswordAuthProvider usernamePasswordAuthProvider;
    private UsernamePasswordFilter usernamePasswordFilter;

    public SecurityConfig(@Lazy JwtAuthProvider jwtAuthProvider, @Lazy JWTFilter jwtFilter, @Lazy UsernamePasswordAuthProvider usernamePasswordAuthProvider, @Lazy UsernamePasswordFilter usernamePasswordFilter) {
        this.jwtAuthProvider = jwtAuthProvider;
        this.jwtFilter = jwtFilter;
        this.usernamePasswordAuthProvider = usernamePasswordAuthProvider;
        this.usernamePasswordFilter = usernamePasswordFilter;
    }


    @Bean
    public RequestMatcher requestMatcher() {
        return new MvcRequestMatcher(new HandlerMappingIntrospector(), "/login");
    }


    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.parentAuthenticationManager(authenticationManager());

    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.
                cors().and().
                httpBasic().disable()
                .csrf().disable()
                .authorizeRequests()
                .mvcMatchers("/register")
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(
                        usernamePasswordFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(usernamePasswordAuthProvider)
                .authenticationProvider(jwtAuthProvider);


    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://localhost:4200");
        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
        corsConfiguration.setExposedHeaders(Arrays.asList("refreshToken", "token"));
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }


}
