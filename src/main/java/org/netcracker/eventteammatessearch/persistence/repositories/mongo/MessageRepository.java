package org.netcracker.eventteammatessearch.persistence.repositories.mongo;

import org.netcracker.eventteammatessearch.dao.MessagesRemainCountData;
import org.netcracker.eventteammatessearch.entity.mongoDB.Message;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, Long> {
    public List<Message> findMessagesByChatId(long chatId);

    @Aggregation(pipeline = {"{$match:{chatId:{$in:?0}}}", "{$sort:{sendTime:-1}}", "{$group:{\n" +
            "  _id: \"$chatId\",\n" +
            "  text:{\n" +
            "    $first:'$text'\n" +
            "  },\n" +
            "  sendTime:{\n" +
            "    $first:'$sendTime'\n" +
            "  },\n" +
            "  userId:{\n" +
            "    $first:'$userId'\n" +
            "  },\n" +
            "  chatId:{\n" +
            "    $first:'$chatId'\n" +
            "  }\n" +
            "}}"})
    public List<Message> findByChatIdInAndOrderBySendTimeDesc(List<Long> chatIds);

    @Aggregation(pipeline = {"{$match:{chatId:{$in:?0}}}", "{$group:{_id:'$chatId'}}"})
    public List<MessagesRemainCountData> countMessagesByChatId(List<Long> chatIds);
}
