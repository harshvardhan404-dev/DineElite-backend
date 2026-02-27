package com.dineelite.backend.repository;

import com.dineelite.backend.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Integer> {
    List<RestaurantTable> findByRestaurant_RestaurantId(Integer restaurantId);

    void deleteByRestaurant(com.dineelite.backend.entity.Restaurant restaurant);
}
