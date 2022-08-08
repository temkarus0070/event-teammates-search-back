package org.netcracker.eventteammatessearch.Services;

import org.netcracker.eventteammatessearch.entity.Event;
import org.netcracker.eventteammatessearch.entity.Location;
import org.netcracker.eventteammatessearch.persistence.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LocationStatsService {

    @Autowired
    private EventRepository eventRepository;

    public Map<Long, Long> getLocationStats(List<Event> eventList) {
        if (eventList == null || eventList.size() == 0)
            return new HashMap<>();
        final int distance = 400;
        Map<Long, Long> resultEventLongMap = new HashMap<>();
        List<Event> events = eventRepository.findByIsOnlineFalseAndDateTimeEndIsNotNullAndDateTimeEndLessThan(LocalDateTime.now());
        Map<Event, Long> eventLongMap = new HashMap<>();
        Map<Location, Long> map = new HashMap<>();
        map.put(events.get(0).getLocation(), (long) events.get(0).getGuests().size());
        eventLongMap.put(events.get(0), (long) events.get(0).getGuests().size());
        if (events.size() > 0) {

            for (int i = 1; i < events.size(); i++) {
                int finalI = i;
                map.forEach((key, value) -> {
                    double distance1 = distance(key.getLocation().getY(), key.getLocation().getX(), events.get(finalI).getLocation().getLocation().getY(), events.get(finalI).getLocation().getLocation().getX()) * 1609.34;
                    if (distance1 < distance) {
                        map.put(key, map.get(key) + events.get(finalI).getGuests().size());
                        eventLongMap.put(events.get(finalI), map.get(key) + events.get(finalI).getGuests().size());
                    } else {
                        eventLongMap.put(events.get(finalI), (long) events.get(finalI).getGuests().size());
                    }

                });
            }

            eventList.forEach(e -> {
                eventLongMap.forEach((key, val) -> {
                    double distance1 = distance(key.getLocation().getLocation().getY(), key.getLocation().getLocation().getX(), e.getLocation().getLocation().getY(), e.getLocation().getLocation().getX()) * 1609.34;
                    if (distance1 < distance) {
                        resultEventLongMap.put(e.getId(), eventLongMap.get(key));
                    }
                });
            });
        }
        return resultEventLongMap;
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
