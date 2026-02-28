package com.dineelite.backend.repository;

import com.dineelite.backend.entity.Restaurant;
import com.dineelite.backend.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {
    List<TimeSlot> findByRestaurant(Restaurant restaurant);

    @Modifying
    @Transactional
    @Query("DELETE FROM TimeSlot s WHERE s.restaurant = :restaurant")
    void deleteByRestaurant(@Param("restaurant") Restaurant restaurant);
}
