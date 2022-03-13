package org.netcracker.eventteammatessearch.security.Persistence.Entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    @Transactional
    @Query("UPDATE UserEntity  u set u.authorities = :user.authorities, u.username= :user.username, u.password = :user.password where u.username= :user.username")
    public void update(@Param("user") UserEntity user);
}
