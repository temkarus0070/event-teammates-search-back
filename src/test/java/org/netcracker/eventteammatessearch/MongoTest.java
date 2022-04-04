package org.netcracker.eventteammatessearch;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.netcracker.eventteammatessearch.entity.mongoDB.EventLengthMark;
import org.netcracker.eventteammatessearch.entity.mongoDB.Review;
import org.netcracker.eventteammatessearch.persistence.repositories.mongo.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.List;


@DataMongoTest
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@Profile("client_app_profile_name")
public class MongoTest {


    @Autowired  ReviewRepository reviewRepository;

    @DisplayName("test avg calculate")
    @Test
    public void test() {
        reviewRepository.save(new Review(new Review.ReviewId(1, "1"), "1", "", 9,
                1, 1, 0.5, EventLengthMark.FINE, new HashSet<>()));
        reviewRepository.save(new Review(new Review.ReviewId(1, "2"), "1", "", 9,
                1, 1, 1, EventLengthMark.FINE, new HashSet<>()));
        reviewRepository.save(new Review(new Review.ReviewId(99, "2"), "228", "", 9,
                1, 1, 1, EventLengthMark.FINE, new HashSet<>()));
        Double avgMark = reviewRepository.averageReviewNumber("1");
        List<Review> all = reviewRepository.findAll();
        Assertions.assertTrue(all.size() == 3);
        Assertions.assertEquals(6.75, avgMark);
        Assertions.assertNull(reviewRepository.averageReviewNumber("222"));
        Assertions.assertEquals(9, reviewRepository.averageReviewNumber("228"));
    }
}
