package org.netcracker.eventteammatessearch.Services.SurveyFilters;

import org.netcracker.eventteammatessearch.entity.Event;
import org.netcracker.eventteammatessearch.entity.Survey;
import org.springframework.stereotype.Component;

@Component
public class TypeFilter implements SurveyFilter{
    @Override
    public boolean isFit(Event event, Survey survey) {
        if (survey.getType().stream().noneMatch(e-> event.getEventType().getName().equalsIgnoreCase(e))) {
            return false;
        }
        return true;
    }
}
