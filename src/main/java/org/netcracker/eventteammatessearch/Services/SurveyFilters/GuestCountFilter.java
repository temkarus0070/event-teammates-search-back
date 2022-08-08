package org.netcracker.eventteammatessearch.Services.SurveyFilters;

import org.netcracker.eventteammatessearch.entity.Event;
import org.netcracker.eventteammatessearch.entity.Survey;
import org.springframework.stereotype.Component;

@Component
public class GuestCountFilter implements SurveyFilter{
    @Override
    public boolean isFit(Event event, Survey survey) {
        int guestsCount = event.getGuests().size();
        if (guestsCount > survey.getMaxNumberOfGuests()) {
            return false;
        }
        if (guestsCount<survey.getMinNumberOfGuests())
            return false;
        return true;
    }
}
