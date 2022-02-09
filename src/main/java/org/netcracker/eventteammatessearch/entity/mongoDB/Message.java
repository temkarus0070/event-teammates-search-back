package org.netcracker.eventteammatessearch.entity.mongoDB;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
@ToString
public class Message {
    @Transient
    public static final String SEQUENCE_NAME = "messages_sequence";

    @Id
    private long id;

    private LocalDateTime sendTime;

    private long chatId;

    private long userId;

    private boolean isRemoved;

    private Set<String> messageImagesUrls;


}
