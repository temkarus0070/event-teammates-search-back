package org.netcracker.eventteammatessearch.security.Services;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.netcracker.eventteammatessearch.security.Entity.JWTAuthentication;
import org.netcracker.eventteammatessearch.security.Persistence.Entity.JwtUserEntity;
import org.netcracker.eventteammatessearch.security.Persistence.Entity.UserDetailsManager;
import org.netcracker.eventteammatessearch.security.Persistence.JwtTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;

@Service
public class JwtTokenGeneratorService {
    @Value("${jwt.secretKey}")
    private String secret;

    @Value("${jwt.expireTime}")
    private long expireTime;

    @Value("${jwt.refreshExpireTime}")
    private long refreshExpire;

    private JwtBuilder jwtBuilder;

    @Autowired
    private JwtTokenRepository jwtTokenRepository;

    @Autowired
    private UserDetailsManager userDetailsManager;

    public JWTAuthentication generate(Authentication userDetails) {
        JwtUserEntity jwtUserEntity = new JwtUserEntity();
        if (jwtBuilder == null)
            jwtBuilder = Jwts.builder();
        Date current = new Date();
        JwtBuilder jwtBuilder = this.jwtBuilder
                .setIssuedAt(current)
                .setExpiration(new Date(current.getTime() + expireTime))
                .claim("username", userDetails.getName())
                .signWith(SignatureAlgorithm.HS512, secret);
        jwtUserEntity.setId(new JwtUserEntity.JwtUserKey(userDetails.getName(), jwtBuilder.compact()));
        jwtUserEntity.setRefreshToken(generateRefresh());
        jwtTokenRepository.save(jwtUserEntity);
        return new JWTAuthentication(jwtUserEntity.getId().getJwt(), secret, jwtUserEntity.getRefreshToken(), userDetailsManager);
    }

    public JwtUserEntity generate(JwtUserEntity userDetails) {
        JwtUserEntity jwtUserEntity = new JwtUserEntity();
        if (jwtBuilder == null)
            jwtBuilder = Jwts.builder();
        Date current = new Date();
        JwtBuilder jwtBuilder = this.jwtBuilder
                .setIssuedAt(current)
                .setExpiration(new Date(current.getTime() + expireTime))
                .claim("username", userDetails.getId().getUsername())
                .signWith(SignatureAlgorithm.HS512, secret);
        jwtUserEntity.setId(new JwtUserEntity.JwtUserKey(userDetails.getId().getUsername(), jwtBuilder.compact()));
        jwtUserEntity.setRefreshToken(generateRefresh());
        jwtTokenRepository.saveAndFlush(jwtUserEntity);
        return jwtUserEntity;
    }

    public String generateRefresh() {
        if (jwtBuilder == null)
            jwtBuilder = Jwts.builder();
        Date current = new Date();
        Random random = new Random(current.getTime());
        return jwtBuilder.setIssuedAt(current)
                .setExpiration(new Date(current.getTime() + refreshExpire))
                .claim("random", random.nextDouble())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public JwtUserEntity refreshToken(JwtUserEntity jwtUser) {
        JwtUserEntity token = jwtTokenRepository.findJwtUserEntityById_JwtAndRefreshToken(jwtUser.getId().getJwt(), jwtUser.getRefreshToken());
        if (token == null) {
          return null;
        } else {
            JwtUserEntity jwtUserEntity = generate(token);
            jwtTokenRepository.delete(token);
            return jwtUserEntity;
        }
    }

}
