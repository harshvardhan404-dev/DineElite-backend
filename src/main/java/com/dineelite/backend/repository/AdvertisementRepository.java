package com.dineelite.backend.repository;

import com.dineelite.backend.entity.Advertisement;
import com.dineelite.backend.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Integer> {
    List<Advertisement> findAllByOrderByCreatedAtDesc();
    List<Advertisement> findByRestaurantRestaurantIdOrderByCreatedAtDesc(Integer restaurantId);

    List<Advertisement> findByRestaurant(Restaurant restaurant);

    @Modifying
    @Transactional
    @Query("DELETE FROM Advertisement a WHERE a.restaurant = :restaurant")
    void deleteByRestaurant(@Param("restaurant") Restaurant restaurant);
}
