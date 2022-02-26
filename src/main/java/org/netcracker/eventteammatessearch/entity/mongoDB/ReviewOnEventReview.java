package org.netcracker.eventteammatessearch.entity.mongoDB;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "reviews")
public class ReviewOnEventReview implements Serializable {
    @Id
    private ReviewOnReviewId id;
    private int weight;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class ReviewOnReviewId {
        private Review.ReviewId reviewId;
        private String reviewerId;
    }
}


