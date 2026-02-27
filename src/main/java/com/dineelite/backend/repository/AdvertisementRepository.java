package com.dineelite.backend.repository;

import com.dineelite.backend.entity.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Integer> {
    List<Advertisement> findAllByOrderByCreatedAtDesc();
    List<Advertisement> findByRestaurantRestaurantIdOrderByCreatedAtDesc(Integer restaurantId);

    List<Advertisement> findByRestaurant(com.dineelite.backend.entity.Restaurant restaurant);
    void deleteByRestaurant(com.dineelite.backend.entity.Restaurant restaurant);
}
