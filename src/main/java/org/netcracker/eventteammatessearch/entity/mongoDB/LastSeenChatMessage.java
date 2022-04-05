package org.netcracker.eventteammatessearch.entity.mongoDB;


import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "last_messages")
@ToString
public class LastSeenChatMessage {
    private long chatId;
    private long messageId;
    private String username;
}
