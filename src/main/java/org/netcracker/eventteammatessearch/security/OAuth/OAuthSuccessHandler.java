package org.netcracker.eventteammatessearch.security.OAuth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.netcracker.eventteammatessearch.entity.User;
import org.netcracker.eventteammatessearch.persistence.repositories.UserRepository;
import org.netcracker.eventteammatessearch.security.Persistence.Entity.JwtUserEntity;
import org.netcracker.eventteammatessearch.security.Persistence.JwtTokenRepository;
import org.netcracker.eventteammatessearch.security.Services.JwtTokenGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;


@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    private OAuthUserExtractor oAuthUserExtractor;

    @Autowired
    private final OAuthTokenGenerator tokenProvider;


    @Autowired
    private AuthRepository authRepository;

    @Value("${FRONTEND}")
    private String frontend;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenRepository jwtTokenRepository;

    @Autowired
    private JwtTokenGeneratorService jwtTokenGeneratorService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (response.isCommitted()) {
            return;
        }
        User user = saveUserIfNew(authentication);
        String targetUrl = determineTargetUrl(request, response, authentication, user);
        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private User saveUserIfNew(Authentication authentication) {
        OAuth2AuthenticationToken auth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        User user = userRepository.findByOauthUserTrueAndOauthServiceAndOauthKey(auth2AuthenticationToken.getAuthorizedClientRegistrationId(), auth2AuthenticationToken.getPrincipal().getName());
        if (user == null) {
            user = oAuthUserExtractor.extract(auth2AuthenticationToken);
        }
        return user;
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication, User user) {
        String url = frontend + "/login/oauth2";
        String token = tokenProvider.createToken(authentication, user);
        if (user.getRegistrationDate()!=null){
            return UriComponentsBuilder.fromUriString(url)
                    .queryParam("token", token)
                    .build().toUriString();
        }
        ObjectMapper objectMapper=new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(user);
            JwtUserEntity jwtUserEntity = new JwtUserEntity(new JwtUserEntity.JwtUserKey(user.getLogin(), token), null, json);
            jwtTokenRepository.save(jwtUserEntity);
        }
        catch (JsonProcessingException processingException){
            processingException.printStackTrace();
        }
        return UriComponentsBuilder.fromUriString(url)
                .queryParam("token", token)
                .queryParam("notRegistered",true)
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authRepository.removeAuthorizationRequestCookies(request, response);
    }

}
