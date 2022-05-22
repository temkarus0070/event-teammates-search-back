package org.netcracker.eventteammatessearch.persistence.repositories;

import org.netcracker.eventteammatessearch.entity.Relationship;
import org.netcracker.eventteammatessearch.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, String> {

    @EntityGraph(value = "relation-graph")
    @Query("select r from Relationship r where r.id.owner=:owner and r.id.friend=:friend")
    Relationship getRelationshipByUsersLogin(User owner, User friend);

    @EntityGraph(value = "relation-graph")
    @Query("select r from Relationship r where r.id.friend=:owner and r.friend=false")
    List<Relationship> getRelationshipsRequestsByOwner(User owner);

    @EntityGraph(value = "relation-graph")
    @Query("select r from Relationship r where r.id.owner=:owner and r.friend=false")
    List<Relationship> getRelationshipSendedRequestsByOwner(User owner);


    @EntityGraph(value = "relation-graph")
    @Query("select r from Relationship r where r.id.friend=:owner and r.friend=true")
    List<Relationship> getRelationshipsFriendsByOwner(User owner);
}
