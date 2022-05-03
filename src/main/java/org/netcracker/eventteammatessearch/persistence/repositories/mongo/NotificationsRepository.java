package org.netcracker.eventteammatessearch.persistence.repositories.mongo;

import org.netcracker.eventteammatessearch.entity.mongoDB.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationsRepository extends MongoRepository<Notification, Long> {
    List<Notification> findAllByUserIdOrderByShownAsc(String userId);

    List<Notification> findAllByUserIdAndIsShownIsFalse(String userId);

}
