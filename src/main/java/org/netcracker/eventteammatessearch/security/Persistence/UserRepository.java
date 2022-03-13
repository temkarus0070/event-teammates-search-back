package org.netcracker.eventteammatessearch.security.Persistence;

import org.netcracker.eventteammatessearch.security.Persistence.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    @Transactional
    @Query("UPDATE UserEntity  u set u.authorities = :authorities, u.username= :username, u.password = :password where u.username= :username")
    public void update(List<GrantedAuthority> authorities, String username, String password);

    @Transactional
    @Query(value = "UPDATE UserEntity user set user.password= :password where user.username= :username", nativeQuery = true)
    public void updatePassword(String password, String username);
}
