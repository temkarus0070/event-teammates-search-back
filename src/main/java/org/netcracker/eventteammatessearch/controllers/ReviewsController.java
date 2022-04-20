package org.netcracker.eventteammatessearch.controllers;

import org.netcracker.eventteammatessearch.Services.ReviewService;
import org.netcracker.eventteammatessearch.entity.mongoDB.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/reviews")
public class ReviewsController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public void add(@RequestBody Review review, Principal principal) {
        reviewService.addReview(review, principal);
    }
}
