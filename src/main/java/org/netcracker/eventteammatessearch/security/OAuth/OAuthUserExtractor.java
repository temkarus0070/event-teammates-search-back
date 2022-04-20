package org.netcracker.eventteammatessearch.security.OAuth;

import org.netcracker.eventteammatessearch.entity.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OAuthUserExtractor {
    public User extract(OAuth2AuthenticationToken authenticationToken) {
        DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authenticationToken.getPrincipal();
        String email = defaultOAuth2User.getAttribute("email");
        String name = defaultOAuth2User.getAttribute("given_name");
        String lastName = defaultOAuth2User.getAttribute("family_name");
        if (name == null) {
            name = defaultOAuth2User.getAttribute("first_name");
        }
        if (lastName == null) {
            lastName = defaultOAuth2User.getAttribute("last_name");
        }
        String pictureUrl = defaultOAuth2User.getAttribute("picture");
        if (pictureUrl == null)
            pictureUrl = defaultOAuth2User.getAttribute("photo_max");
        User user = new User();
        user.setLogin(authenticationToken.getAuthorizedClientRegistrationId() + "-" + defaultOAuth2User.getName());
        user.setFirstName(name);
        user.setEmail(email);
        user.setPictureUrl(pictureUrl);
        user.setLastName(lastName);
        user.setOauthUser(true);
        user.setOauthService(authenticationToken.getAuthorizedClientRegistrationId());
        user.setOauthKey(defaultOAuth2User.getName());
        user.setAuthorities(List.of(new SimpleGrantedAuthority("user")));
        return user;
    }
}
