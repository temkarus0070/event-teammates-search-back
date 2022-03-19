package org.netcracker.eventteammatessearch.entity.mongoDB;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "reviews")
public class Review implements Serializable {
    @Id
    private ReviewId id;

    private String eventOwnerId;

    private String text;

    private int eventMark;

    private int eventOrganizationMark;

    private int recommendToOthersMark;

    private double reviewWeight;

    private EventLengthMark eventLengthMark;

    private Set<String> reviewImagesUrls;


    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class ReviewId implements Serializable {
        private long eventId;

        private String userId;


    }
}
