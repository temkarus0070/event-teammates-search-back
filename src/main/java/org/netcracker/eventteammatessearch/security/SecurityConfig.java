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
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity(debug = true)
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
        auth.authenticationProvider(usernamePasswordAuthProvider);

    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable();
        http.cors()
                .disable()
                .csrf().disable()
                .authorizeRequests()
                .mvcMatchers("/register")
                .permitAll()
                .anyRequest().authenticated()

//            .and()
//            .formLogin()
//            .loginProcessingUrl("/login")
//            .usernameParameter("login")
//            .passwordParameter("password")
//            .successHandler(this::loginSuccessHandler)
//            .failureHandler(this::loginFailureHandler)

                .and()
                .addFilterBefore(
                        usernamePasswordFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .logout()
                .logoutUrl("/logout")

                .and()
                .authenticationProvider(usernamePasswordAuthProvider)
                .exceptionHandling()
        ;


    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
