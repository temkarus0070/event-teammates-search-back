package org.netcracker.eventteammatessearch.Services.SurveyFilters;

import org.netcracker.eventteammatessearch.entity.Event;
import org.netcracker.eventteammatessearch.entity.Survey;
import org.springframework.stereotype.Component;

@Component
public class OnlineOfflineFilter implements SurveyFilter {
    @Override
    public boolean isFit(Event event, Survey survey) {
        if (event.isOnline() && survey.getFormat().stream().noneMatch(e -> e.equalsIgnoreCase("online"))) {
            return false;
        }
        if (!event.isOnline() && survey.getFormat().stream().noneMatch(e -> e.equalsIgnoreCase("offline")))
            return false;
        return true;
    }
}
