package org.netcracker.eventteammatessearch.persistence.repositories.mongo;

import org.netcracker.eventteammatessearch.entity.mongoDB.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Message, Long> {
}
