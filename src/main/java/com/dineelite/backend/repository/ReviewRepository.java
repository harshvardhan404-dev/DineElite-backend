package com.dineelite.backend.repository;

import com.dineelite.backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByRestaurant_RestaurantIdOrderByCreatedAtDesc(Integer restaurantId);
    List<Review> findByUser_UserIdOrderByCreatedAtDesc(Integer userId);

    void deleteByRestaurant_RestaurantId(Integer restaurantId);
    void deleteByUser_UserId(Integer userId);
}
