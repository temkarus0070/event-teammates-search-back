package org.netcracker.eventteammatessearch.persistence.repositories;

import org.netcracker.eventteammatessearch.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @EntityGraph(value = "user-graph")
    User findUserByPhoneContains(String phone);

    @EntityGraph(value = "user-graph")
    @Query("select u from User u")
    List<User> findAll();

    @Transactional
    @Query("UPDATE User u set u.authorities = :authorities, u.login= :username, u.password = :password where u.login= :login")
    public void update(List<GrantedAuthority> authorities, String login, String password);

    @Transactional
    @Query(value = "UPDATE User user set user.password= :password where user.login= :login", nativeQuery = true)
    public void updatePassword(String password, String login);

    @EntityGraph(value = "user-graph")
    @Query(value = "select u from User u where lower(u.login) like lower(:login) and u.login not like :userLogin")
    List<User> getUsersListByLoginWithValidation(String login, String userLogin);


    @EntityGraph(value = "user-graph")
    @Query(value = "select u from User u where u.login like :login")
    List<User> getUsersByLogin(String login);

    @EntityGraph(value = "user-graph")
    @Query(value = "select u from User u where u.login like :login")
    User getUserByLogin(String login);

    @EntityGraph(value = "user-graph")
    @Query(value = "select u from User u where ((lower(u.firstName) like lower(:firstName) and lower(u.lastName) like lower(:lastName)) or (lower(u.firstName) like lower(:lastName) and lower(u.lastName) like lower(:firstName))) and lower(u.login) not like lower(:userLogin)")
    List<User> getUsersByName(String firstName, String lastName, String userLogin);

    @EntityGraph(value = "user-graph")
    @Query(value = "select u from User u where (lower(u.firstName) like lower(:name) or lower(u.lastName) like lower(:name)) and lower(u.login) not like lower(:userLogin)")
    List<User> getUsersByName(String name, String userLogin);

    @EntityGraph(value = "user-graph")
    User findByOauthUserTrueAndOauthServiceAndOauthKey(String oauthService, String key);

    @EntityGraph(value = "user-graph")
    List<User> getUsersByEmail(String mail);

    @EntityGraph(value = "user-graph")
    @Query("select u from User  u join u.authorities a where a=?1")
    List<User> findUsersByAuthorities(String grantedAuthority);

    @EntityGraph(value = "user-graph")
    List<User> findUsersByPhone(String phone);

}
