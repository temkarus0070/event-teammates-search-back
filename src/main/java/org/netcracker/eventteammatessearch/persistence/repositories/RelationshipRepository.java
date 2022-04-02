package org.netcracker.eventteammatessearch.persistence.repositories;

import org.netcracker.eventteammatessearch.entity.Relationship;
import org.netcracker.eventteammatessearch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, String> {

    @Query("select r from Relationship r where owner=:owner and friend=:friend")
    Relationship getRelationshipByUsersLogin(User owner, User friend);
}
