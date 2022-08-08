package org.netcracker.eventteammatessearch.Services.SurveyFilters;

import org.netcracker.eventteammatessearch.entity.Event;
import org.netcracker.eventteammatessearch.entity.Survey;
import org.springframework.stereotype.Component;

@Component
public interface SurveyFilter {
    public boolean isFit(Event event, Survey survey);
}
