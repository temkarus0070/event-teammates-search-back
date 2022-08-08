package org.netcracker.eventteammatessearch.Services.SurveyFilters;

import org.netcracker.eventteammatessearch.entity.Event;
import org.netcracker.eventteammatessearch.entity.Survey;
import org.springframework.stereotype.Component;

@Component
public class DatetimeFilter implements SurveyFilter{
    @Override
    public boolean isFit(Event event, Survey survey) {
        if (event.getDateTimeStart().isBefore(survey.getDateTimeStart())){
            return false;
        }
        if (event.getDateTimeEnd()!=null&&event.getDateTimeEnd().isAfter(survey.getDateTimeEnd())){
            return false;
        }
        return true;
    }
}
