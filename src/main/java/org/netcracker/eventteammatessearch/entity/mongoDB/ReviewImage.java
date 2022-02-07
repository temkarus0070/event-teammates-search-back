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
@ToString
@Document(collection = "review_images")
public class ReviewImage implements Serializable {
    @Transient
    public static final String SEQUENCE_NAME = "review_images_sequence";

    @Id
    private long id;

    private String url;

    private long eventId;

    private long userId;
}
