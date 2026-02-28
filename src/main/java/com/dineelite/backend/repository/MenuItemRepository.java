package com.dineelite.backend.repository;

import com.dineelite.backend.entity.MenuItem;
import com.dineelite.backend.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {
    List<MenuItem> findByRestaurantRestaurantIdAndIsAvailableTrue(Integer restaurantId);
    List<MenuItem> findByRestaurantRestaurantId(Integer restaurantId);

    @Modifying
    @Transactional
    @Query("DELETE FROM MenuItem m WHERE m.restaurant = :restaurant")
    void deleteByRestaurant(@Param("restaurant") Restaurant restaurant);
}
