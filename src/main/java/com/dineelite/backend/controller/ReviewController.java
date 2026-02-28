package com.dineelite.backend.controller;

import com.dineelite.backend.dto.ReviewRequest;
import com.dineelite.backend.dto.ReviewResponse;
import com.dineelite.backend.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ReviewResponse> addReview(
            org.springframework.security.core.Authentication authentication,
            @RequestPart("review") ReviewRequest request,
            @RequestPart(value = "photos", required = false) MultipartFile[] photos) {
        return ResponseEntity.ok(reviewService.addReview(request, photos, authentication.getName()));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<ReviewResponse>> getRestaurantReviews(@PathVariable Integer restaurantId) {
        return ResponseEntity.ok(reviewService.getReviewsForRestaurant(restaurantId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponse>> getUserReviews(@PathVariable Integer userId) {
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId));
    }
}
