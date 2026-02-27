package com.dineelite.backend.repository;

import com.dineelite.backend.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {
    List<MenuItem> findByRestaurantRestaurantIdAndIsAvailableTrue(Integer restaurantId);
    List<MenuItem> findByRestaurantRestaurantId(Integer restaurantId);

    void deleteByRestaurant(com.dineelite.backend.entity.Restaurant restaurant);
}
