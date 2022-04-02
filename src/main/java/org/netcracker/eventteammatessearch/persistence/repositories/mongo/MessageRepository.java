package org.netcracker.eventteammatessearch.persistence.repositories.mongo;

import org.netcracker.eventteammatessearch.entity.mongoDB.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, Long> {
    public List<Message> findMessagesByChatId(long chatId);
}
