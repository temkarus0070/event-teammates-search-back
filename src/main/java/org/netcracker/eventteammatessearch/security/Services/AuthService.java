package org.netcracker.eventteammatessearch.security.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.HibernateException;
import org.netcracker.eventteammatessearch.entity.User;
import org.netcracker.eventteammatessearch.persistence.repositories.UserRepository;
import org.netcracker.eventteammatessearch.security.Entity.JWTAuthentication;
import org.netcracker.eventteammatessearch.security.Persistence.Entity.JwtUserEntity;
import org.netcracker.eventteammatessearch.security.Persistence.JwtTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AuthService {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
private JwtTokenRepository jwtTokenRepository;
    @Autowired
private UserRepository userRepository;

        @Autowired
    private JwtTokenGeneratorService jwtTokenGeneratorService;


public JwtUserEntity register(JwtUserEntity jwtUserEntity) throws Exception {
    JwtUserEntity jwtUserEntityById_jwt = jwtTokenRepository.findJwtUserEntityById_Jwt(jwtUserEntity.getId().getJwt());
    if (jwtUserEntityById_jwt!=null){
            if (jwtUserEntityById_jwt.getRefreshToken()==null ) {
                if (jwtUserEntityById_jwt.getUserData() != null) {

                        User user = objectMapper.readValue(jwtUserEntityById_jwt.getUserData(), User.class);
                    user.setLogin(jwtUserEntity.getId().getUsername());
                   if(userRepository.findById(user.getLogin()).isPresent()) {
                       throw new HibernateException("Такое имя пользователя уже занято");
                   }
                   user.setRegistrationDate(LocalDate.now());
                        userRepository.save(user);
                    JWTAuthentication newJWT = jwtTokenGeneratorService.generate(new UsernamePasswordAuthenticationToken(jwtUserEntity.getId().getUsername(), null));
                    jwtUserEntity=new JwtUserEntity(new JwtUserEntity.JwtUserKey(newJWT.getName(), newJWT.getCredentials().toString()),newJWT.getRefreshToken(),null);
                        jwtTokenRepository.delete(jwtUserEntityById_jwt);
                        jwtTokenRepository.save(jwtUserEntity);
                        return jwtUserEntity;
                    }
                }
            }

    return jwtUserEntity;
}
}
