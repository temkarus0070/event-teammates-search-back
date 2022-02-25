package org.netcracker.eventteammatessearch.persistence.repositories.mongo;

import org.netcracker.eventteammatessearch.entity.mongoDB.Review;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface ReviewRepository extends MongoRepository<Review, Review.ReviewId> {
    @Aggregation(pipeline = {"{'$match':{'eventOwnerId': ?0}}",
    "{$group:{ _id: '', total: {$avg: {'$multiply': ['$eventMark','$reviewWeight']}}  }}"})
    public Double averageReviewNumber( long ownerId);
}
