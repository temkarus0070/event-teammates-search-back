package org.netcracker.eventteammatessearch.persistence.repositories;

import org.netcracker.eventteammatessearch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Transactional
    @Query("UPDATE User u set u.authorities = :authorities, u.login= :username, u.password = :password where u.login= :login")
    public void update(List<GrantedAuthority> authorities, String login, String password);

    @Transactional
    @Query(value = "UPDATE User user set user.password= :password where user.login= :login", nativeQuery = true)
    public void updatePassword(String password, String login);

    @Query(value = "select u from User u where u.login like :login")
    List<User> getUsersByLogin(String login);

    @Query(value = "select u from User u where (u.firstName like :firstName and u.lastName like :lastName) or (u.firstName like :lastName and u.lastName like :firstName)")
    List<User> getUsersByName(String firstName, String lastName);
}
