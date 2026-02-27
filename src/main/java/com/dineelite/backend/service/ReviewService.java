package com.dineelite.backend.service;

import com.dineelite.backend.dto.ReviewRequest;
import com.dineelite.backend.dto.ReviewResponse;
import com.dineelite.backend.entity.Restaurant;
import com.dineelite.backend.entity.Review;
import com.dineelite.backend.entity.ReviewPhoto;
import com.dineelite.backend.entity.User;
import com.dineelite.backend.repository.RestaurantRepository;
import com.dineelite.backend.repository.ReviewRepository;
import com.dineelite.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private FileUploadService fileUploadService;

    @Transactional
    public ReviewResponse addReview(ReviewRequest request, MultipartFile[] photos) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        Review review = new Review();
        review.setUser(user);
        review.setRestaurant(restaurant);
        review.setRating(request.getRating());
        review.setContent(request.getContent());

        // Handle uploaded photos
        if (photos != null && photos.length > 0) {
            for (MultipartFile photoFile : photos) {
                if (!photoFile.isEmpty()) {
                    String url = fileUploadService.saveFile(photoFile);
                    ReviewPhoto photo = new ReviewPhoto();
                    photo.setPhotoUrl(url);
                    photo.setReview(review);
                    review.getPhotos().add(photo);
                }
            }
        }

        // Handle provided URLs (optional)
        if (request.getPhotoUrls() != null) {
            for (String url : request.getPhotoUrls()) {
                ReviewPhoto photo = new ReviewPhoto();
                photo.setPhotoUrl(url);
                photo.setReview(review);
                review.getPhotos().add(photo);
            }
        }

        Review savedReview = reviewRepository.save(review);
        return mapToResponse(savedReview);
    }

    public List<ReviewResponse> getReviewsForRestaurant(Integer restaurantId) {
        return reviewRepository.findByRestaurant_RestaurantIdOrderByCreatedAtDesc(restaurantId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ReviewResponse> getReviewsByUser(Integer userId) {
        return reviewRepository.findByUser_UserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ReviewResponse mapToResponse(Review review) {
        List<String> photoUrls = review.getPhotos().stream()
                .map(ReviewPhoto::getPhotoUrl)
                .collect(Collectors.toList());

        return new ReviewResponse(
                review.getReviewId(),
                review.getUser().getFullName(),
                review.getRestaurant().getName(),
                review.getRating(),
                review.getContent(),
                review.getCreatedAt(),
                photoUrls
        );
    }
}
