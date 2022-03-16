package org.netcracker.eventteammatessearch.security.Services;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.netcracker.eventteammatessearch.security.Entity.JWTAuthentication;
import org.netcracker.eventteammatessearch.security.Persistence.Entity.JwtUserEntity;
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

    public JWTAuthentication generate(Authentication userDetails) {
        JwtUserEntity jwtUserEntity = new JwtUserEntity();
        if (jwtBuilder == null)
            jwtBuilder = Jwts.builder();
        Date current = new Date();
        JwtBuilder jwtBuilder = this.jwtBuilder
                .setIssuedAt(current)
                .setExpiration(new Date(current.getTime() + expireTime))
                .claim("username", userDetails.getName())
                .claim("roles", userDetails.getAuthorities())
                .signWith(SignatureAlgorithm.HS512, secret);
        jwtUserEntity.setJwtUserKey(new JwtUserEntity.JwtUserKey(userDetails.getName(), jwtBuilder.compact()));
        jwtUserEntity.setRefreshToken(generateRefresh());
        jwtTokenRepository.save(jwtUserEntity);
        return new JWTAuthentication(jwtUserEntity.getJwtUserKey().getJwt(), secret, jwtUserEntity.getRefreshToken());
    }

    private String generateRefresh() {
        Date current = new Date();
        Random random = new Random(current.getTime());
        return jwtBuilder.setIssuedAt(current)
                .setExpiration(new Date(current.getTime() + refreshExpire))
                .claim("random", random.nextDouble())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

}
