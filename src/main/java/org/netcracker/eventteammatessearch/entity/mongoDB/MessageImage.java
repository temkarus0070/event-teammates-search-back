package org.netcracker.eventteammatessearch.entity.mongoDB;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages_images")
@ToString
public class MessageImage {
    @Transient
    public static final String SEQUENCE_NAME = "messages_images_sequence";

    @Id
    private Long id;

    private String imageURL;

}
