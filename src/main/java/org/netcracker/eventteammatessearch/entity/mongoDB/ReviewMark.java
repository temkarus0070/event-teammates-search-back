package org.netcracker.eventteammatessearch.entity.mongoDB;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Document(collection = "reviewMarks")
public class ReviewMark implements Serializable {
    @Id
    private ReviewMarkId id;

    private boolean hasLike;
}

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
class ReviewMarkId implements Serializable {
    private String userId;
    private long reviewId;
}
