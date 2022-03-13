package org.netcracker.eventteammatessearch.security.Persistence;

import org.netcracker.eventteammatessearch.security.Persistence.Entity.JwtUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtUserEntity, JwtUserEntity.JwtUserKey> {
}
