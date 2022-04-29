package org.netcracker.eventteammatessearch.controllers;

import org.netcracker.eventteammatessearch.Services.ReviewService;
import org.netcracker.eventteammatessearch.entity.mongoDB.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewsController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public void add(@RequestBody Review review, Principal principal) {
        reviewService.addReview(review, principal);
    }

    @GetMapping("/getReviewsForUserEvents")
    public List<Review> getReviewsForUserEvents(@RequestParam String login){
        return reviewService.getReviewsForUserEvents(login);
    }
}
