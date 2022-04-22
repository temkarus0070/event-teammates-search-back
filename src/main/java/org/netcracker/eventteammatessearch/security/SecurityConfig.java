package org.netcracker.eventteammatessearch.security;

import org.netcracker.eventteammatessearch.security.Filters.JWTFilter;
import org.netcracker.eventteammatessearch.security.Filters.UsernamePasswordFilter;
import org.netcracker.eventteammatessearch.security.OAuth.CustomOAuth2UserService;
import org.netcracker.eventteammatessearch.security.OAuth.CustomTokenResponseConverter;
import org.netcracker.eventteammatessearch.security.OAuth.OAuthSuccessHandler;
import org.netcracker.eventteammatessearch.security.Providers.JwtAuthProvider;
import org.netcracker.eventteammatessearch.security.Providers.UsernamePasswordAuthProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity()
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Value("${FRONTEND}")
    private String frontendAddress;
    private JwtAuthProvider jwtAuthProvider;
    private JWTFilter jwtFilter;
    private UsernamePasswordAuthProvider usernamePasswordAuthProvider;
    private UsernamePasswordFilter usernamePasswordFilter;

    @Autowired
    private OAuthSuccessHandler oAuthSuccessHandler;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(@Lazy JwtAuthProvider jwtAuthProvider, @Lazy JWTFilter jwtFilter, @Lazy UsernamePasswordAuthProvider usernamePasswordAuthProvider, @Lazy UsernamePasswordFilter usernamePasswordFilter) {
        this.jwtAuthProvider = jwtAuthProvider;
        this.jwtFilter = jwtFilter;
        this.usernamePasswordAuthProvider = usernamePasswordAuthProvider;
        this.usernamePasswordFilter = usernamePasswordFilter;
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .mvcMatchers("/chatService/**", "/ws/**");
    }

    @Bean
    public RequestMatcher requestMatcher() {
        return new MvcRequestMatcher(new HandlerMappingIntrospector(), "/login");
    }


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.
                cors().and().
                httpBasic().disable()
                .csrf().disable()
                .authorizeRequests()
                .mvcMatchers("/register", "/refreshToken", "/api/events/getEventsWithinRadius", "/api/events/filter", "/api/eventTypes/**", "/error/**", "/oauth2/**", "/login**", "/generateRefreshToken","/completeOauth")
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login().successHandler(oAuthSuccessHandler)
                .tokenEndpoint()
                .accessTokenResponseClient(accessTokenResponseClient())
                //Userinfo endpoint
                .and()
                .userInfoEndpoint()
                .userService(customOAuth2UserService)
                .and().and()
                .addFilterAt(
                        usernamePasswordFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(usernamePasswordAuthProvider)
                .authenticationProvider(jwtAuthProvider);


    }


    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        DefaultAuthorizationCodeTokenResponseClient accessTokenResponseClient =
                new DefaultAuthorizationCodeTokenResponseClient();
        OAuth2AccessTokenResponseHttpMessageConverter tokenResponseHttpMessageConverter =
                new OAuth2AccessTokenResponseHttpMessageConverter();
        tokenResponseHttpMessageConverter.setTokenResponseConverter(new CustomTokenResponseConverter());
        RestTemplate restTemplate = new RestTemplate(Arrays.asList(
                new FormHttpMessageConverter(), tokenResponseHttpMessageConverter));
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
        accessTokenResponseClient.setRestOperations(restTemplate);
        return accessTokenResponseClient;
    }

    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin(frontendAddress);
        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
        corsConfiguration.setExposedHeaders(Arrays.asList("refreshToken", "token"));
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }


}
