package com.dineelite.backend.repository;

import com.dineelite.backend.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
    Optional<Restaurant> findByAdminUserId(Integer userId);
    List<Restaurant> findByCuisine(String cuisine);
}
