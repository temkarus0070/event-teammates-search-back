package org.netcracker.eventteammatessearch.appEntities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.netcracker.eventteammatessearch.entity.EventType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventFilterData {
    private String keyWords;
    private EventType eventType;
    private long eventLengthFrom;
    private long eventLengthTo;
    private LocalDateTime eventBeginTimeFrom;
    private LocalDateTime eventBeginTimeTo;
    private int guestsCountFrom;
    private int guestsCountTo;
    private double priceFrom;
    private double priceTo;
    private String theme;
    private double maxDistance;
    private double eventOwnerRating;
    private String eventFormat;


}
