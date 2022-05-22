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
    @Query("select r from Relationship r where r.id.ownerId=:owner and r.id.friendId=:friend")
    Relationship getRelationshipByUsersLogin(String owner, String friend);

    @EntityGraph(value = "relation-graph")
    @Query("select r from Relationship r where r.id.friendId=:owner and r.isItFriend=false")
    List<Relationship> getRelationshipsRequestsByOwner(String owner);

    @EntityGraph(value = "relation-graph")
    @Query("select r from Relationship r where r.id.ownerId=:owner and r.isItFriend=false")
    List<Relationship> getRelationshipSendedRequestsByOwner(String owner);


    @EntityGraph(value = "relation-graph")
    @Query("select r from Relationship r where r.id.friendId=:owner and r.isItFriend=true")
    List<Relationship> getRelationshipsFriendsByOwner(String owner);
}
