package org.netcracker.eventteammatessearch.persistence.repositories.mongo;

import org.netcracker.eventteammatessearch.dao.ReviewData;
import org.netcracker.eventteammatessearch.entity.mongoDB.Review;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<Review, Review.ReviewId> {
    @Aggregation(pipeline = {"{$match:{'eventOwnerId': ?0}}",
            "{$group:{ _id: '', total: {$avg: {$multiply: ['$eventMark','$reviewWeight']}}  }}"})
    Double averageReviewNumber(String ownerId);

    List<Review> getReviewsById_UserIdAndId_EventIdIn(String login, List<Long> ids);


    @Aggregation(pipeline = {"{$match:{'eventOwnerId': {'$in':?0}}}",
            "{$group:{ _id: '$eventOwnerId', mark: {$avg: {$multiply: ['$eventMark','$reviewWeight']}}  }}", "{'$addFields':{'eventOwnerId':'$_id'}}"})
    List<ReviewData> getReviewsByEventOwnerIdIsIn(List<String> login);


}
