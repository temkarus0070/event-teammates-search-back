package org.netcracker.eventteammatessearch.Services.SurveyFilters;

import org.netcracker.eventteammatessearch.entity.Event;
import org.netcracker.eventteammatessearch.entity.Survey;
import org.springframework.stereotype.Component;

@Component
public class PriceFilter implements SurveyFilter{
    @Override
    public boolean isFit(Event event, Survey survey) {
        if (!(event.getPrice()<=survey.getMaxPrice()&&event.getPrice()>=survey.getMinPrice())){
            return false;
        }
        return true;
    }
}
