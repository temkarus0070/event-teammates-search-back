package org.netcracker.eventteammatessearch.entity.mongoDB;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "notificationDescriptions")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NotificationDescription {
    @Transient
    public static final String SEQUENCE_NAME = "notification_descriptions_sequence";

    @Id
    private long id;

    private LocalDateTime notificationTime;

    private String title;

    private String description;
}
