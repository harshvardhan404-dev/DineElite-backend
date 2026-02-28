package com.dineelite.backend.repository;

import com.dineelite.backend.entity.Restaurant;
import com.dineelite.backend.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Integer> {
    List<RestaurantTable> findByRestaurant_RestaurantId(Integer restaurantId);

    @Query("""
        SELECT t FROM RestaurantTable t
        WHERE t.restaurant.restaurantId = :restaurantId
        AND t.capacity >= :guestCount
        AND t.tableId NOT IN (
            SELECT bt.table.tableId FROM BookingTable bt
            WHERE bt.bookingDate = :date
            AND bt.slot.slotId = :slotId
            AND bt.booking.status = 'CONFIRMED'
        )
    """)
    List<RestaurantTable> findAvailableTables(
        @Param("restaurantId") Integer restaurantId,
        @Param("date") java.time.LocalDate date,
        @Param("slotId") Integer slotId,
        @Param("guestCount") Integer guestCount
    );

    @Modifying
    @Transactional
    @Query("DELETE FROM RestaurantTable t WHERE t.restaurant = :restaurant")
    void deleteByRestaurant(@Param("restaurant") Restaurant restaurant);
}
