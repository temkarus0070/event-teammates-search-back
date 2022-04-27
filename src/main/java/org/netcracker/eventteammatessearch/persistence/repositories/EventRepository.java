package org.netcracker.eventteammatessearch.persistence.repositories;

import org.locationtech.jts.geom.Point;
import org.netcracker.eventteammatessearch.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;


@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {


    @Query(value = "SELECT e from Event  e where distance(e.location.location, :p,true) <= :distanceM")
    List<Event> findNearWithinDistance(Point p, double distanceM);

    @Query(value = "SELECT e FROM Event e inner join e.tags inner join e.guests inner join e.location inner join e.eventType inner join e.owner inner join e.invitedGuests")
    List<Event> findAll(List<Specification<Event>> specifications);

    @Query(value = "SELECT e FROM Event e where e.owner.login = :userLogin")
    List<Event> getUsersCreatedEventsByLogin(String userLogin);

    @Query(value = "SELECT e FROM Event e inner join e.tags inner join e.guests inner join e.location inner join e.eventType inner join e.owner inner join e.invitedGuests")
    Page<Event> findAll(List<Specification<Event>> specifications, Pageable pageable);

    @Query(value = "select  t2.word\n" +
            "from event t1\n" +
            "  cross join lateral unnest(string_to_array(t1.description,' ')) as t2 (word)\n" +
            "where t2.word like concat(:word,'%')\n" +
            "union\n" +
            "select t3.word\n" +
            "from event t1\n" +
            "  cross join lateral unnest(string_to_array(t1.name,' ')) as t3 (word)\n" +
            "where t3.word like concat(:word,'%');", nativeQuery = true)
    Set<String> getWords(String word);

    @Query("SELECT e from Event e where e.dateTimeEnd is not null  and e.dateTimeEnd<CURRENT_TIMESTAMP and exists (SELECT  ea from EventAttendance  ea where  ea.user.login=:login and ea.event=e)")
    List<Event> findAllUserEndedEvents(String login);

    @Query("SELECT e from Event e inner join User u on u member of e.invitedGuests and u.login=:login")
    List<Event> findUsersInvitedEvents(String login);
}
