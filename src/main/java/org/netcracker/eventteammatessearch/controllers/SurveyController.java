package org.netcracker.eventteammatessearch.controllers;


import org.netcracker.eventteammatessearch.entity.Survey;
import org.netcracker.eventteammatessearch.entity.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class SurveyController {

    @PostMapping("/api/survey")
    public Survey addSurvey(@RequestBody Survey data, Principal principal) {
        String name = principal.getName();
        User user = new User();
        user.setLogin(name);
        data.setUser(user);
        user.setSurveyResult(data);
/*
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\nTypes: ");
        for (int i = 0; i < user.getSurveyResult().getType().size(); i++) System.out.print(user.getSurveyResult().getType().get(i) + " ");
        System.out.println("\nFormats");
        for (int i = 0; i < user.getSurveyResult().getFormat().size(); i++) System.out.print(user.getSurveyResult().getFormat().get(i));
        System.out.println("date: " + user.getSurveyResult().getDateTimeStart() + " - " + user.getSurveyResult().getDateTimeEnd());
        System.out.println("members: " + user.getSurveyResult().getMinNumberOfGuests() + " - " + user.getSurveyResult().getMaxNumberOfGuests());
        System.out.println("money: " + user.getSurveyResult().getMinPrice() + " - " + user.getSurveyResult().getMaxPrice());
        System.out.println("location: " + user.getSurveyResult().getLocation());
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
*/
        return data;
    }
}
