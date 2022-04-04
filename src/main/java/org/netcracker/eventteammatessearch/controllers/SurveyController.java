package org.netcracker.eventteammatessearch.controllers;


import org.netcracker.eventteammatessearch.entity.Survey;
import org.netcracker.eventteammatessearch.entity.User;
import org.netcracker.eventteammatessearch.persistence.repositories.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class SurveyController {

    @Autowired
    private SurveyRepository surveyRepository;

    @PostMapping("/api/survey")
    public Survey addSurvey(@RequestBody Survey data, Principal principal) {
        String name = principal.getName();
        User user = new User();
        user.setLogin(name);
        data.setUser(user);
        user.setSurveyResult(data);
        Survey temp = surveyRepository.findByUser_login(name);
        if (temp != null) {
            surveyRepository.delete(temp);
        }
        surveyRepository.save(data);
/*
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\nTypes: ");
        for (int i = 0; i < user.getSurveyResult().getType().size(); i++) System.out.print(user.getSurveyResult().getType().get(i) + " ");
        System.out.println("\nFormats");
        for (int i = 0; i < user.getSurveyResult().getFormat().size(); i++) System.out.print(user.getSurveyResult().getFormat().get(i));
        System.out.println("date: " + user.getSurveyResult().getDateTimeStart() + " - " + user.getSurveyResult().getDateTimeEnd());
        System.out.println("members: " + user.getSurveyResult().getMinNumberOfGuests() + " - " + user.getSurveyResult().getMaxNumberOfGuests());
        System.out.println("money: " + user.getSurveyResult().getMinPrice() + " - " + user.getSurveyResult().getMaxPrice());
        System.out.println("location: " + user.getSurveyResult().getLocation());
        System.out.println("user: " + user.getSurveyResult().getUser());
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
*/
        //surveyRepository.deleteById(name);
        return data;
    }

    @GetMapping("/api/surveyCheck")
    public boolean surveyCheck(Principal principal) {
        if (surveyRepository.findByUser_login(principal.getName()) != null) { return true; }
        else { return false; }
    }
}
