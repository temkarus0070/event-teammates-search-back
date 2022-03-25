package org.netcracker.eventteammatessearch.persistence.repositories;

import org.locationtech.jts.geom.Point;
import org.netcracker.eventteammatessearch.entity.Event;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {


    @Query(value = "SELECT e from Event  e where distance(e.location.location, :p,true) <= :distanceM")
    List<Event> findNearWithinDistance(Point p, double distanceM);

    @Query(value = "SELECT e FROM Event e inner join e.tags inner join e.guests inner join e.location inner join e.eventType inner join e.owner inner join e.invitedGuests")
    List<Event> findAll(List<Specification<Event>> specifications);
}
