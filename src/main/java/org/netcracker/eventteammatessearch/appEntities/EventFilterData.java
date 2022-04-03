package org.netcracker.eventteammatessearch.appEntities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.netcracker.eventteammatessearch.entity.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventFilterData {
    private List<String> keyWords;
    private EventType eventType;
    private long eventLengthFrom;
    private long eventLengthTo;
    private LocalDateTime eventBeginTimeFrom;
    private LocalDateTime eventBeginTimeTo;
    private int guestsCountFrom;
    private int guestsCountTo;
    private float priceFrom;
    private float priceTo;
    private String theme;
    private double maxDistance;
    private double eventOwnerRating;
    private List<String> eventFormats;
    private double[] userLocation;
    private boolean freeEvents;


}
