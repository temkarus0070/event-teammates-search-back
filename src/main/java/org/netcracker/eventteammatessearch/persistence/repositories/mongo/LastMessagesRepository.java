package org.netcracker.eventteammatessearch.persistence.repositories.mongo;

import org.netcracker.eventteammatessearch.entity.mongoDB.LastSeenChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LastMessagesRepository extends MongoRepository<LastSeenChatMessage, LastSeenChatMessage.LastSeenMessageId> {

    List<LastSeenChatMessage> findAllById_ChatIdInAndId_Username(List<Long> ids, String username);
}
