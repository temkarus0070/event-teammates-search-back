package org.netcracker.eventteammatessearch.security.Filters;

import org.netcracker.eventteammatessearch.security.Entity.JWTAuthentication;
import org.netcracker.eventteammatessearch.security.Services.JwtTokenGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
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
        Authentication authenticate = this.getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(request.getHeader("username"), request.getHeader("password")));
        if (authenticate.isAuthenticated()) {
            JWTAuthentication generate = jwtTokenGeneratorService.generate(authenticate);
            response.setHeader("token", String.valueOf(generate.getCredentials()));
            response.setHeader("refreshToken", String.valueOf(generate.getDetails()));
        }
        return authenticate;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        super.doFilter(request, response, chain);
    }

}
