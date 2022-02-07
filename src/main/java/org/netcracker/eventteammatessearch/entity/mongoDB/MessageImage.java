package org.netcracker.eventteammatessearch.entity.mongoDB;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "messages_images")
@ToString
public class MessageImage implements Serializable {
    @Transient
    public static final String SEQUENCE_NAME = "messages_images_sequence";

    @Id
    private Long id;

    private String imageURL;

}
