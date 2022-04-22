package org.netcracker.eventteammatessearch.security.OAuth;

import io.jsonwebtoken.*;
import org.netcracker.eventteammatessearch.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class OAuthTokenGenerator {
    private static Logger log = LoggerFactory.getLogger(OAuthTokenGenerator.class);
    @Value("${jwt.secretKey}")
    private String secret;

    @Value("${jwt.tempSecretKey}")
    private String tempSecret;

    @Value("${jwt.expireTime}")
    private long expireTime;

    @Value("${jwt.refreshExpireTime}")
    private long refreshExpire;


    public String createToken(Authentication authentication, User user) {
        OAuth2AuthenticationToken auth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String key = defaultOAuth2User.getName();

        Date now = new Date();

        Date expiryDate = new Date(now.getTime() + expireTime);

        if (user.getRegistrationDate()!=null)
        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .claim("username", user.getLogin())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
        else
            return Jwts.builder()
                    .setIssuedAt(new Date())
                    .setExpiration(expiryDate)
                    .claim("username", user.getLogin())
                    .signWith(SignatureAlgorithm.HS512,tempSecret)
                    .compact();
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }
}
