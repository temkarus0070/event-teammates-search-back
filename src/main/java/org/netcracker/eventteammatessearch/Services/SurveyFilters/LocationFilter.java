package org.netcracker.eventteammatessearch.Services.SurveyFilters;

import org.netcracker.eventteammatessearch.entity.Event;
import org.netcracker.eventteammatessearch.entity.Survey;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LocationFilter implements SurveyFilter{
    @Override
    public boolean isFit(Event event, Survey survey) {
        if (survey.getFormat().contains("Offline") &&!event.isOnline() &&!getCityFromAddress(event.getLocation().getName()).equalsIgnoreCase(survey.getLocation())){
            return false;
        }
        return true;
    }
    private String getCityFromAddress(String address){
        String city="";
        Pattern patternOnlyWithCity=Pattern.compile("г\\.[а-яА-Я]*");
        Matcher onlyAddressWithCityMatcher = patternOnlyWithCity.matcher(address);
        if (onlyAddressWithCityMatcher.find()) {
            city = onlyAddressWithCityMatcher.group().substring(2);
        }
        return city;
    }
}
