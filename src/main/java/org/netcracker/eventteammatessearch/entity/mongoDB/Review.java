package org.netcracker.eventteammatessearch.entity.mongoDB;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Document(collection = "reviews")
public class Review {
    @Id
    private ReviewId id;

    private String text;

    private int eventMark;

    private int eventOrganizationMark;

    private int recommendToOthersMark;

    private double reviewWeight;

    private EventLengthMark eventLengthMark;

    private List<ReviewImage> images;


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
