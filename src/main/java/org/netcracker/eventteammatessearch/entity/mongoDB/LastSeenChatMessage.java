package org.netcracker.eventteammatessearch.entity.mongoDB;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "last_messages")
@ToString
public class LastSeenChatMessage {

    @Id
    private LastSeenMessageId id;
    private long messageId;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LastSeenMessageId {
        private long chatId;
        private String username;
    }
}

