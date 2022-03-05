package org.netcracker.eventteammatessearch.persistence.repositories;

import org.locationtech.jts.geom.Point;
import org.netcracker.eventteammatessearch.entity.Event;
import org.netcracker.eventteammatessearch.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query(value = "SELECT * from location where ST_DistanceSphere(geom, :p) < :distanceM", nativeQuery = true)
    List<Location> findNearWithinDistance(Point p, int distanceM);
}
