package com.dineelite.backend.repository;

import com.dineelite.backend.entity.Restaurant;
import com.dineelite.backend.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {
    List<TimeSlot> findByRestaurant(Restaurant restaurant);
}
