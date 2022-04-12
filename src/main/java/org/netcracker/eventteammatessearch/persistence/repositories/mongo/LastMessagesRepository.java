package org.netcracker.eventteammatessearch.persistence.repositories.mongo;

import org.netcracker.eventteammatessearch.entity.mongoDB.LastSeenChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LastMessagesRepository extends MongoRepository<LastSeenChatMessage, LastSeenChatMessage.LastSeenMessageId> {
}
