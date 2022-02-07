package org.netcracker.eventteammatessearch.entity.mongoDB;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Document(collection = "reviews")
public class Review implements Serializable {
    @Id
    private ReviewId id;

    private String text;

    private int eventMark;

    private int eventOrganizationMark;

    private int recommendToOthersMark;

    private double reviewWeight;

    private EventLengthMark eventLengthMark;

    private


    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    static class ReviewId implements Serializable {
        private long eventId;

        private long userId;


    }
}
