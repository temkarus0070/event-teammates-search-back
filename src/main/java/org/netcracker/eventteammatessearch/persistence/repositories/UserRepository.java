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

    @Query(value = "select u from User u where lower(u.login) like lower(:login) and u.login not like :userLogin")
    List<User> getUsersListByLoginWithValidation(String login, String userLogin);

    @Query(value = "select u from User u where u.login like :login")
    List<User> getUsersByLogin(String login);

    @Query(value = "select u from User u where u.login like :login")
    User getUserByLogin(String login);

    @Query(value = "select u from User u where ((lower(u.firstName) like lower(:firstName) and lower(u.lastName) like lower(:lastName)) or (lower(u.firstName) like lower(:lastName) and lower(u.lastName) like lower(:firstName))) and lower(u.login) not like lower(:userLogin)")
    List<User> getUsersByName(String firstName, String lastName, String userLogin);

    @Query(value = "select u from User u where (lower(u.firstName) like lower(:name) or lower(u.lastName) like lower(:name)) and lower(u.login) not like lower(:userLogin)")
    List<User> getUsersByName(String name, String userLogin);

    User findByOauthUserTrueAndOauthServiceAndOauthKey(String oauthService, String key);
}
