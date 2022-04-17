package org.netcracker.eventteammatessearch.security.OAuth;

import lombok.RequiredArgsConstructor;
import org.netcracker.eventteammatessearch.entity.User;
import org.netcracker.eventteammatessearch.persistence.repositories.UserRepository;
import org.netcracker.eventteammatessearch.security.Persistence.JwtTokenRepository;
import org.netcracker.eventteammatessearch.security.Services.JwtTokenGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

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
            DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
            String email = defaultOAuth2User.getAttribute("email");
            String name = defaultOAuth2User.getAttribute("name");
            String pictureUrl = defaultOAuth2User.getAttribute("picture");
            user = new User();
            user.setLogin(auth2AuthenticationToken.getAuthorizedClientRegistrationId() + "-" + defaultOAuth2User.getName());
            if (name != null)
                user.setFirstName(name);
            if (email != null) {
                user.setEmail(email);
            }
            if (pictureUrl != null) {
                user.setPictureUrl(pictureUrl);
            }
            user.setOauthUser(true);
            user.setOauthService(auth2AuthenticationToken.getAuthorizedClientRegistrationId());
            user.setOauthKey(defaultOAuth2User.getName());
            user = userRepository.save(user);
        }
        return user;
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication, User user) {
        String url = frontend + "/login/oauth2";
        String token = tokenProvider.createToken(authentication, user);

        return UriComponentsBuilder.fromUriString(url)
                .queryParam("token", token)
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authRepository.removeAuthorizationRequestCookies(request, response);
    }

}
