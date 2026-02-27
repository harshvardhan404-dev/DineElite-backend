package com.dineelite.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewResponse {
    private Integer reviewId;
    private String userName;
    private String restaurantName;
    private Integer rating;
    private String content;
    private LocalDateTime createdAt;
    private List<String> photoUrls;

    public ReviewResponse(Integer reviewId, String userName, String restaurantName, Integer rating, String content, LocalDateTime createdAt, List<String> photoUrls) {
        this.reviewId = reviewId;
        this.userName = userName;
        this.restaurantName = restaurantName;
        this.rating = rating;
        this.content = content;
        this.createdAt = createdAt;
        this.photoUrls = photoUrls;
    }

    // Getters and Setters
    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }
}
