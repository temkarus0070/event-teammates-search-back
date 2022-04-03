package org.netcracker.eventteammatessearch.security.Filters;

import org.netcracker.eventteammatessearch.security.Entity.JWTAuthentication;
import org.netcracker.eventteammatessearch.security.Persistence.Entity.UserDetailsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class JWTFilter extends AbstractAuthenticationProcessingFilter {
    @Value("${jwt.secretKey}")
    private String secretKey;

    @Autowired
    private UserDetailsManager userDetailsManager;

    protected JWTFilter(@Value("/api/**") String defaultFilterProcessesUrl, AuthenticationManager authenticationManager) {
        super(defaultFilterProcessesUrl, authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        String authorizationToken = request.getHeader("Authorization");
        if (authorizationToken == null) {
            authorizationToken = response.getHeader("token");
        }
        Authentication authenticate = null;
        if (authorizationToken != null)
            authenticate = this.getAuthenticationManager().authenticate(new JWTAuthentication(authorizationToken, secretKey, userDetailsManager));
        else throw new BadCredentialsException("you dont have token");
        return authenticate;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        super.doFilter(request, response, chain);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authResult);
        chain.doFilter(request, response);
    }


}
