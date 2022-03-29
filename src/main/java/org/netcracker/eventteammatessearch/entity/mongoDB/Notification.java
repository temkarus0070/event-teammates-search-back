package org.netcracker.eventteammatessearch.entity.mongoDB;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "events")
@ToString
public class Notification {
    @Transient
    public static final String SEQUENCE_NAME = "events_sequence";

    @Id
    private long id;

    private String userId;

    private NotificationDescription notificationDescription;

    private boolean isShown;
}
