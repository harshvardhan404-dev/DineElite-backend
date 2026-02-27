package com.dineelite.backend.repository;

import com.dineelite.backend.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByUser_UserIdOrderByBookingDateDesc(Integer userId);

    Long countByRestaurant_RestaurantId(Integer restaurantId);

    @Query("""
        SELECT b.slot.slotId, COUNT(b)
        FROM Booking b
        WHERE b.restaurant.restaurantId = :restaurantId
        AND b.status = com.dineelite.backend.enums.BookingStatus.CONFIRMED
        GROUP BY b.slot.slotId
        ORDER BY COUNT(b) DESC
    """)
    List<Object[]> findPeakSlotByRestaurant(@Param("restaurantId") Integer restaurantId);

    @Query("""
        SELECT b.bookingDate, COUNT(b)
        FROM Booking b
        WHERE b.restaurant.restaurantId = :restaurantId
        AND b.status = com.dineelite.backend.enums.BookingStatus.CONFIRMED
        GROUP BY b.bookingDate
        ORDER BY b.bookingDate ASC
    """)
    List<Object[]> findDailyBookingTrend(@Param("restaurantId") Integer restaurantId);

    @Query("""
        SELECT SUM(b.depositAmount)
        FROM Booking b
        WHERE b.restaurant.restaurantId = :restaurantId
        AND b.status = com.dineelite.backend.enums.BookingStatus.CONFIRMED
    """)
    Double calculateTotalRevenue(@Param("restaurantId") Integer restaurantId);

    @Query("""
        SELECT COUNT(b)
        FROM Booking b
        WHERE b.restaurant.restaurantId = :restaurantId
        AND b.bookingDate = :bookingDate
        AND b.slot.slotId = :slotId
        AND b.status = 'CONFIRMED'
        """)
    Long countConfirmedForSlot(
            @Param("restaurantId") Integer restaurantId,
            @Param("bookingDate") LocalDate bookingDate,
            @Param("slotId") Integer slotId
    );

    @Query(value = """
        SELECT EXTRACT(DOW FROM b.booking_date) as dow, s.start_time, COUNT(b.booking_id)
        FROM bookings b
        JOIN time_slots s ON b.slot_id = s.slot_id
        WHERE b.restaurant_id = :restaurantId
        AND b.status = 'CONFIRMED'
        GROUP BY dow, s.start_time
        ORDER BY dow, s.start_time
        """, nativeQuery = true)
    List<Object[]> findHeatmapData(@Param("restaurantId") Integer restaurantId);

    List<Booking> findByRestaurant(com.dineelite.backend.entity.Restaurant restaurant);
    List<Booking> findByUser(com.dineelite.backend.entity.User user);
}
