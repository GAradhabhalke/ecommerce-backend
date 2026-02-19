package com.company.ecommerce.controller;

import com.company.ecommerce.dtos.CreateReviewRequestDto;
import com.company.ecommerce.entity.Review;
import com.company.ecommerce.service.ReviewService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review createReview(Principal principal, @RequestBody CreateReviewRequestDto request) {
        return reviewService.createReview(principal.getName(), request.getProductId(), request.getRating(), request.getComment());
    }

    @DeleteMapping("/{reviewId}")
    public void deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
    }
}
