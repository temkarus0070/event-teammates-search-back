package org.netcracker.eventteammatessearch.security.Persistence;

import org.netcracker.eventteammatessearch.security.Persistence.Entity.JwtUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Transactional
@Repository
public interface JwtTokenRepository extends JpaRepository<JwtUserEntity, JwtUserEntity.JwtUserKey> {


    JwtUserEntity findJwtUserEntityById_JwtAndRefreshToken(String jwt, String refreshToken);

    JwtUserEntity findJwtUserEntityById_Jwt(String jwt);
}

