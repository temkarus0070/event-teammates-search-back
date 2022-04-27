package org.netcracker.eventteammatessearch.persistence.repositories;

import org.netcracker.eventteammatessearch.entity.Event;
import org.netcracker.eventteammatessearch.entity.EventAttendance;
import org.netcracker.eventteammatessearch.entity.UserEventKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventAttendanceRepository extends JpaRepository<EventAttendance, UserEventKey> {

    @Query(value = "SELECT e.event FROM EventAttendance e where e.id.userId = :userLogin")
    List<Event> getUsersAttendedEventsByLogin(String userLogin);

}
