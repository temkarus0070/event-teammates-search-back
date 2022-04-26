package org.netcracker.eventteammatessearch.security.Filters;

import org.netcracker.eventteammatessearch.security.Entity.JWTAuthentication;
import org.netcracker.eventteammatessearch.security.Persistence.Entity.UserDetailsManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class JWTFilter extends AbstractAuthenticationProcessingFilter {
    @Value("${jwt.secretKey}")
    private String secretKey;
    private UserDetailsManager userDetailsManager;

    protected JWTFilter(@Value("/api/**") String defaultFilterProcessesUrl, AuthenticationManager authenticationManager, @Lazy UserDetailsManager userDetailsManager) {
        super(defaultFilterProcessesUrl, authenticationManager);
        this.userDetailsManager = userDetailsManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String authorizationToken = request.getHeader("Authorization");
        return this.getAuthenticationManager().authenticate(new JWTAuthentication(authorizationToken, secretKey, userDetailsManager));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.sendError(403);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);
        chain.doFilter(request, response);

    }
}
