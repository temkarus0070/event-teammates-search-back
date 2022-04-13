package org.netcracker.eventteammatessearch.persistence.repositories.mongo;

import org.netcracker.eventteammatessearch.entity.mongoDB.Message;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, Long> {
    public List<Message> findMessagesByChatIdOrderBySendTime(long chatId);

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
            "  },\n" +
            "messageId:{$first:'$_id'}" +
            "}}", "{$addFields:{\n" +
            "  id: '$messageId'\n" +
            "}}"})
    public List<Message> findByChatIdInAndOrderBySendTimeDesc(List<Long> chatIds);

    public void removeMessageByChatIdAndIdAndUserId(@Param("chatId") long chatId, @Param("id") long id, String userId);

    public Message findTopByChatIdAndIdBeforeOrderByIdDesc(long chatId, long id);
}
