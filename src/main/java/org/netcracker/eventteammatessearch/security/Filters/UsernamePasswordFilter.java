package org.netcracker.eventteammatessearch.security.Filters;

import org.netcracker.eventteammatessearch.security.Entity.JWTAuthentication;
import org.netcracker.eventteammatessearch.security.Services.JwtTokenGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class UsernamePasswordFilter extends AbstractAuthenticationProcessingFilter {

    @Autowired
    private JwtTokenGeneratorService jwtTokenGeneratorService;

    public UsernamePasswordFilter(RequestMatcher requiresAuthenticationRequestMatcher, AuthenticationManager authenticationManager) {
        super(requiresAuthenticationRequestMatcher, authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getHeader("username");
        String password = request.getHeader("password");
        if (username != null && password != null)
            return this.getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(username, password));
        else throw new BadCredentialsException("you have not typed your login or pass");
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authenticate) throws IOException, ServletException {
        if (authenticate.isAuthenticated()) {
            JWTAuthentication generate = jwtTokenGeneratorService.generate(authenticate);
            response.setHeader("token", String.valueOf(generate.getCredentials()));
            response.setHeader("refreshToken", String.valueOf(generate.getDetails()));
            SecurityContextHolder.getContext().setAuthentication(generate);
        }
        chain.doFilter(request, response);
    }


}