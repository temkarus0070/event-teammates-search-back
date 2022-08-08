package org.netcracker.eventteammatessearch.Services;

import org.netcracker.eventteammatessearch.Services.SurveyFilters.SurveyFilter;
import org.netcracker.eventteammatessearch.entity.Event;
import org.netcracker.eventteammatessearch.entity.Survey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SurveyFilterService {
    @Autowired
    private List<SurveyFilter> surveyFilters = new ArrayList<>();

    public boolean isFit(Survey survey, Event event) {
        for (SurveyFilter surveyFilter : surveyFilters) {
            boolean fit = surveyFilter.isFit(event, survey);
            if (!fit)
                return false;
        }
        return true;
    }
}
