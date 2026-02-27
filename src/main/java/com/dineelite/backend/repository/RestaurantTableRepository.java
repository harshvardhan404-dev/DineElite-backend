package com.dineelite.backend.repository;

import com.dineelite.backend.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Integer> {
    List<RestaurantTable> findByRestaurant_RestaurantId(Integer restaurantId);

    @org.springframework.data.jpa.repository.Query("""
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
        @org.springframework.data.repository.query.Param("restaurantId") Integer restaurantId,
        @org.springframework.data.repository.query.Param("date") java.time.LocalDate date,
        @org.springframework.data.repository.query.Param("slotId") Integer slotId,
        @org.springframework.data.repository.query.Param("guestCount") Integer guestCount
    );

    void deleteByRestaurant(com.dineelite.backend.entity.Restaurant restaurant);
}
